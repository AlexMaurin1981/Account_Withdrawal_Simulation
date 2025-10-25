package user.service;

import user.model.TransferRequest;

public interface TransferServer {

     TransferStatus processTransfer(TransferRequest transferRequest);


}
