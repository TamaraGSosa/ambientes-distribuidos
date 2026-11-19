import { Controller, Get } from '@nestjs/common';
import { InjectDataSource } from '@nestjs/typeorm';
import { DataSource } from 'typeorm';
import { extractDbErrorMessage } from '../database/db-error.util';

interface NodeHealth {
  status: 'up' | 'down';
  role: 'primary' | 'replica';
  inRecovery?: boolean;
  error?: string;
}

@Controller('health')
export class HealthController {
  constructor(
    @InjectDataSource('primary')
    private readonly primaryDataSource: DataSource,
    @InjectDataSource('replica')
    private readonly replicaDataSource: DataSource,
  ) {}

  @Get()
  async check() {
    const [primary, replica] = await Promise.all([
      this.checkNode(this.primaryDataSource, 'primary'),
      this.checkNode(this.replicaDataSource, 'replica'),
    ]);

    const overallStatus =
      primary.status === 'up' && replica.status === 'up'
        ? 'ok'
        : primary.status === 'down' && replica.status === 'down'
          ? 'down'
          : 'degraded';

    return {
      status: overallStatus,
      timestamp: new Date().toISOString(),
      nodes: {
        primary,
        replica,
      },
    };
  }

  private async checkNode(
    dataSource: DataSource,
    role: 'primary' | 'replica',
  ): Promise<NodeHealth> {
    try {
      const result: Array<{ in_recovery: boolean }> = await dataSource.query(
        'SELECT pg_is_in_recovery() AS in_recovery',
      );
      return {
        status: 'up',
        role,
        inRecovery: Boolean(result[0]?.in_recovery),
      };
    } catch (error) {
      return {
        status: 'down',
        role,
        error: extractDbErrorMessage(error),
      };
    }
  }
}
