package user.service;

public enum TransferStatus {

    SUCCESS,
    INVALID_REQUEST,
    ACCOUNT_NOT_FOUND, // не найден
    ACCOUNT_FROZEN, // (для любого из счетов)
    INVALID_AMOUNT, //(сумма не положительная)
    SAME_ACCOUNT_TRANSFER, //(попытка перевода на тот же счет)
    INSUFFICIENT_FUNDS, //(недостаточно средств на счете-источнике)
    TRANSFER_FAILED, //(общий статус, если произошла ошибка на этапе атомарной операции, которую нельзя классифицировать иначе)
}
