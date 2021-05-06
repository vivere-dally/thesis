import { Entity } from "../../core/entity";

export enum CurrencyType {
    RON = "RON",
    EUR = "EUR",
    USD = "USD"
}

export interface Account extends Entity<number> {
    money: number;
    monthlyIncome: number;
    currency: CurrencyType;
}
