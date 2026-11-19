import {
  Injectable,
  Logger,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { extractDbErrorMessage } from '../database/db-error.util';
import { CreateRecordDto } from './dto/create-record.dto';
import { UpdateRecordDto } from './dto/update-record.dto';
import { RecordEntity } from './entities/record.entity';

@Injectable()
export class RecordsService {
  private readonly logger = new Logger(RecordsService.name);

  constructor(
    @InjectRepository(RecordEntity, 'primary')
    private readonly primaryRepo: Repository<RecordEntity>,
    @InjectRepository(RecordEntity, 'replica')
    private readonly replicaRepo: Repository<RecordEntity>,
  ) {}

  async findAll() {
    try {
      const records = await this.replicaRepo.find({
        order: { id: 'ASC' },
      });
      return {
        node: 'replica',
        count: records.length,
        data: records,
      };
    } catch (error) {
      this.handleNodeError('replica', 'SELECT ALL', error);
    }
  }

  async findOne(id: number) {
    let record: RecordEntity | null;
    try {
      record = await this.replicaRepo.findOneBy({ id });
    } catch (error) {
      this.handleNodeError('replica', `SELECT id=${id}`, error);
    }

    if (!record) {
      throw new NotFoundException(`Registro con id ${id} no encontrado en replica.`);
    }

    return {
      node: 'replica',
      data: record,
    };
  }

  async create(dto: CreateRecordDto) {
    try {
      const entity = this.primaryRepo.create({
        name: dto.name,
        description: dto.description ?? null,
      });
      const saved = await this.primaryRepo.save(entity);
      this.logger.log(`INSERT en primary exitoso (id=${saved.id})`);
      return {
        node: 'primary',
        data: saved,
      };
    } catch (error) {
      this.handleNodeError('primary', 'INSERT', error);
    }
  }

  async update(id: number, dto: UpdateRecordDto) {
    let existing: RecordEntity | null;
    try {
      existing = await this.primaryRepo.findOneBy({ id });
    } catch (error) {
      this.handleNodeError('primary', `UPDATE lookup id=${id}`, error);
    }

    if (!existing) {
      throw new NotFoundException(`Registro con id ${id} no encontrado en primary.`);
    }

    if (dto.name !== undefined) {
      existing.name = dto.name;
    }
    if (dto.description !== undefined) {
      existing.description = dto.description;
    }

    try {
      const updated = await this.primaryRepo.save(existing);
      this.logger.log(`UPDATE en primary exitoso (id=${updated.id})`);
      return {
        node: 'primary',
        data: updated,
      };
    } catch (error) {
      this.handleNodeError('primary', `UPDATE id=${id}`, error);
    }
  }

  async remove(id: number) {
    let existing: RecordEntity | null;
    try {
      existing = await this.primaryRepo.findOneBy({ id });
    } catch (error) {
      this.handleNodeError('primary', `DELETE lookup id=${id}`, error);
    }

    if (!existing) {
      throw new NotFoundException(`Registro con id ${id} no encontrado en primary.`);
    }

    try {
      await this.primaryRepo.remove(existing);
      this.logger.log(`DELETE en primary exitoso (id=${id})`);
      return {
        node: 'primary',
        deletedId: id,
      };
    } catch (error) {
      this.handleNodeError('primary', `DELETE id=${id}`, error);
    }
  }

  private handleNodeError(
    node: 'primary' | 'replica',
    operation: string,
    error: unknown,
  ): never {
    const message = extractDbErrorMessage(error);
    this.logger.error(
      `Fallo en nodo ${node.toUpperCase()} durante ${operation}: ${message}`,
    );
    throw new ServiceUnavailableException({
      statusCode: 503,
      error: 'Service Unavailable',
      node,
      operation,
      message: `El nodo ${node} no esta disponible o fallo al procesar la operacion (${operation}).`,
      detail: message,
    });
  }
}
