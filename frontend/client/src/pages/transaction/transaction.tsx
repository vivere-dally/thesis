import { Entity } from "../../core/entity";

export enum TransactionType {
    INCOME = "INCOME",
    EXPENSE = "EXPENSE"
}

export interface Transaction extends Entity<number> {
    message: string;
    value: number;
    type: TransactionType;
    date: string;
}

export interface TransactionSumsPerMonth {
    month: number;
    income: number;
    expense: number;
}
