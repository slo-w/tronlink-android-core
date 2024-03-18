package org.tron;

import com.google.protobuf.ByteString;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.tron.api.GrpcAPI;
import org.tron.api.WalletGrpc;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.ByteUtil;
import org.tron.common.utils.LogUtils;
import org.tron.common.utils.TransactionDataUtils;
import org.tron.common.utils.TransactionUtils;
import org.tron.common.utils.abi.AbiUtil;
import org.tron.config.Parameter;
import org.tron.net.input.TriggerContractRequest;
import org.tron.protos.Protocol;
import org.tron.protos.contract.AssetIssueContractOuterClass;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.walletserver.AddressUtil;
import org.tron.walletserver.ConnectErrorException;
import org.tron.walletserver.I_TYPE;
import org.tron.walletserver.Wallet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TransactionCoreUnitTest {
    public static final String TAG_TRANSACTION = "transaction";
    public static final String TAG_ADDRESS = "Address";
    String tgtIp = "grpc.trongrid.io:50051";

    private static final String WALLET_NAME = "walletTest";
    private static final String PRIVETE_KEY = "8e0de1652bb518de63f8a1b8b6d3ad195c069024abe59e4b31fc8a70e124879a";
    private static final String WALLET_ADDRESS_TO = "TDqGdq76PDHrEXfEPMmNa2ayc7E4PKzfS1";
    private static final String WALLET_ADDRESS = "TTjjGfE7xC3FLECERoKuokGRHj4STjF1q4";
    private static final String TOKEN_ID = "1004114";
    private static final String CONTRACT_ADDRESS = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    private static final long AMOUNT = 1000000l;


    Wallet testWallet = null;
    private ManagedChannel channel = null;
    private WalletGrpc.WalletBlockingStub stub;

    @Test
    public void importWalletWithPrivateKey() {
        testWallet = new Wallet(I_TYPE.PRIVATE, PRIVETE_KEY);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_IMPORT_PRIKEY);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println(TAG_ADDRESS + " = " + testWallet.getAddress());
    }

    @Test
    public void connectNode() {
        importWalletWithPrivateKey();
        channel = ManagedChannelBuilder.forTarget(tgtIp)
                .usePlaintext()
                .build();
        stub = WalletGrpc.newBlockingStub(channel);
        ByteString addressBS = ByteString.copyFrom(AddressUtil.decode58Check(WALLET_ADDRESS));
        Protocol.Account request = Protocol.Account.newBuilder().setAddress(addressBS).build();
        Protocol.Account account = stub.withDeadlineAfter(10, TimeUnit.SECONDS).getAccount(request);
        Assert.assertNotNull(account);
        System.out.println(account.getBalance());
        //if wallet is Activated use the code below
        //assertEquals(WALLET_ADDRESS, AddressUtil.encode58Check(account.getAddress().toByteArray()));
    }

    @Test
    public void trxTransactionTransactionTest() {
        initBlockStub();
        Protocol.Transaction transaction = createTrxTransferTransaction(WALLET_ADDRESS, WALLET_ADDRESS_TO, AMOUNT);
        Assert.assertNotNull(transaction);
    }

    @Test
    public void trc20TransactionTransactionTest() {
        initBlockStub();
        Protocol.Transaction transaction = createTrc20TransferTransaction(WALLET_ADDRESS, WALLET_ADDRESS_TO, CONTRACT_ADDRESS);
        Assert.assertNotNull(transaction);
    }

    @Test
    public void trc10TransctionTransactionTest() {
        initBlockStub();
        Protocol.Transaction transaction = createTrc10TransferTransaction(WALLET_ADDRESS, WALLET_ADDRESS_TO, TOKEN_ID, AMOUNT);
        Assert.assertNotNull(transaction);
    }

    @Test
    public void signTest() {
        importWalletWithPrivateKey();
        initBlockStub();
        Protocol.Transaction transactionSigned = sign(createTrxTransferTransaction(WALLET_ADDRESS, WALLET_ADDRESS_TO, AMOUNT));
        Assert.assertNotNull(transactionSigned);
    }

    @Test
    public void broadcastTransactionTest() {
        boolean sent;
        importWalletWithPrivateKey();
        initBlockStub();
        Protocol.Transaction transactionSigned = sign(createTrxTransferTransaction(WALLET_ADDRESS, WALLET_ADDRESS_TO, AMOUNT));
        sent = broadcastTransaction(transactionSigned);
        Assert.assertTrue(sent);
    }

    private Protocol.Transaction sign(Protocol.Transaction transaction) {
        //sign
        Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(transaction);
        mTransactionSigned = TransactionUtils.sign(mTransactionSigned, testWallet.getECKey());
        System.out.println(TAG_TRANSACTION + " = " + mTransactionSigned.toString());
        return mTransactionSigned;
    }

    public boolean broadcastTransaction(Protocol.Transaction mTransactionSigned) {

        boolean sent = false;
        GrpcAPI.Return aReturn;
        if (!TransactionUtils.validTransaction(mTransactionSigned)) aReturn = null;
        else {
            aReturn = stub.broadcastTransaction(mTransactionSigned);
        }

        return TransactionUtils.validTransaction(mTransactionSigned)
                && aReturn == null ? false : aReturn.getResult();
    }


    public void initBlockStub() {
        channel = ManagedChannelBuilder.forTarget(tgtIp)
                .usePlaintext()
                .build();
        stub = WalletGrpc.newBlockingStub(channel);
    }

    public Protocol.Transaction createTrxTransferTransaction(String ownerAddress, String toAddress, long amount) {
        channel = ManagedChannelBuilder.forTarget(tgtIp)
                .usePlaintext()
                .build();
        stub = WalletGrpc.newBlockingStub(channel);
        byte[] owner = AddressUtil.decode58Check(ownerAddress);
        byte[] to = AddressUtil.decode58Check(toAddress);
        // trx
        BalanceContract.TransferContract.Builder builder = BalanceContract.TransferContract.newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsOwner = ByteString.copyFrom(owner);
        builder.setToAddress(bsTo);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);
        BalanceContract.TransferContract transferContract = builder.build();
        Protocol.Transaction transaction = null;
        GrpcAPI.TransactionExtention transactionExtentionTRX = stub.
                withDeadlineAfter(8, TimeUnit.SECONDS).createTransaction2(transferContract);
        if (transactionExtentionTRX.hasResult() && transactionExtentionTRX.getTransaction().toString().length() > 0) {
            transaction = transactionExtentionTRX.getTransaction();
        }
        System.out.println(TAG_TRANSACTION + " = " + transaction.toString());
        return transaction;
    }

    public Protocol.Transaction createTrc10TransferTransaction(String ownerAddress, String toAddress, String tokenIdStr, long amount) {
        channel = ManagedChannelBuilder.forTarget(tgtIp)
                .usePlaintext()
                .build();
        stub = WalletGrpc.newBlockingStub(channel);
        byte[] owner = AddressUtil.decode58Check(ownerAddress);
        byte[] to = AddressUtil.decode58Check(toAddress);
        byte[] tokenId = tokenIdStr.getBytes();
        Protocol.Transaction transaction = null;
        GrpcAPI.TransactionExtention transferExtention = null;
        AssetIssueContractOuterClass.TransferAssetContract contract = null;

        AssetIssueContractOuterClass.TransferAssetContract.Builder transferAssetContractBuilder =
                AssetIssueContractOuterClass.TransferAssetContract.newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsName = ByteString.copyFrom(tokenId);
        ByteString bsOwner = ByteString.copyFrom(owner);
        transferAssetContractBuilder.setToAddress(bsTo);
        transferAssetContractBuilder.setAssetName(bsName);
        transferAssetContractBuilder.setOwnerAddress(bsOwner);
        transferAssetContractBuilder.setAmount(amount);
        contract = transferAssetContractBuilder.build();
        transferExtention = stub.transferAsset2(contract);
        if (transferExtention.hasResult() && transferExtention.getTransaction().toString().length() > 0) {
            transaction = transferExtention.getTransaction();
        }
        System.out.println(TAG_TRANSACTION + " = " + transaction.toString());
        return transaction;

    }

    public Protocol.Transaction createTrc20TransferTransaction(String ownerAddress, String toAddress, String contractAddress) {
        //trx20
        TriggerContractRequest triggerContractRequest = new TriggerContractRequest();
        String methodStr = TransactionDataUtils.transferMethod;
        GrpcAPI.TransactionExtention transactionExtention = null;
        triggerContractRequest.setMethodStr(methodStr);
        triggerContractRequest.setArgsStr(toAddress + "," + AMOUNT);
        triggerContractRequest.setFeeLimit(Parameter.ResConstant.feeLimit);
        triggerContractRequest.setContractAddrStr(contractAddress);
        triggerContractRequest.setHex(false);
        triggerContractRequest.setTokenCallValue(0);
        triggerContractRequest.setOwer(AddressUtil.decodeFromBase58Check(ownerAddress));


        //get TransactionExtention

        String contractAddrStr = triggerContractRequest.getContractAddrStr();
        String methodString = triggerContractRequest.getMethodStr();
        String argsStr = triggerContractRequest.getArgsStr();
        byte[] ower = triggerContractRequest.getOwer();
        boolean isHex = triggerContractRequest.isHex();
        long feeLimit = triggerContractRequest.getFeeLimit();
        long callValue = triggerContractRequest.getCallValue();
        long tokenCallValue = triggerContractRequest.getTokenCallValue();


        byte[] inputBytes;
        if (triggerContractRequest.isAbiPro()) {
            String methodId = triggerContractRequest.getMethodABI();
            String encode = ByteArray.toHexString(AbiUtil.encodeInput(triggerContractRequest.getMethodStr(), triggerContractRequest.getArgsStr()));
            inputBytes = ByteArray.fromHexString(methodId + encode);
        } else {
            inputBytes = Hex.decode(AbiUtil.parseMethod(methodStr, argsStr, isHex));
        }

        byte[] contractAddressBytes = AddressUtil.decodeFromBase58Check(contractAddrStr);


        SmartContractOuterClass.TriggerSmartContract.Builder triggerContractBuild

                = SmartContractOuterClass.TriggerSmartContract.newBuilder();
        if (!ByteUtil.isNullOrZeroArray(ower))
            triggerContractBuild.setOwnerAddress(ByteString.copyFrom(ower));
        if (!ByteUtil.isNullOrZeroArray(contractAddressBytes))
            triggerContractBuild.setContractAddress(ByteString.copyFrom(contractAddressBytes));
        if (!ByteUtil.isNullOrZeroArray(inputBytes))
            triggerContractBuild.setData(ByteString.copyFrom(inputBytes));
        triggerContractBuild.setCallValue(callValue);


        transactionExtention = stub.triggerConstantContract(triggerContractBuild.build());

        Protocol.Transaction transaction = transactionExtention.getTransaction();
        System.out.println(TAG_TRANSACTION + " = " + transaction.toString());
        return transaction;
    }

}
