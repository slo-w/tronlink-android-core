package org.tron.metrics.bean;

public enum ActionType {
    unKnow(-1),
    ContractType(1),
    //transfer
    LocalTrxTransfer(1051),
    LocalTrc10Transfer(1052),
    LocalTrc20Transfer(1053),
    LocalTrc721Transfer(1054),
    LocalStakeTrx(1061),
    LocalVoteTrx(1062),
    LocalUnstakeTrx(1063),
    LocalWithDrawTrx(1064),
    LocalDelegateEnergy(1065),
    LocalDelegateBandwidth(1066),
    LocalUnDelegateEnergy(1067),
    LocalUnDelegateBandwidth(1068),
    LocalVoteReward(1069),
    LocalCancelVote(1070),

    LocalUpdatepermission(1081),
    LocalSwapApprove(1082),
    LocalSwap(1083),

    //dapp
    DappApprove(1071),
    DappTrxTransfer(1072),
    DappTrc10Transfer(1073),
    DappTrc20Transfer(1074),
    DappTrc721Transfer(1075),
    DappTriggerSmartContract(1076),

    //justlend
    JustLendDeposit(1001),
    JustLendWithdraw(1002),
    JustLendClaimReward(1003),
    //finance
    FinancialStakeTrx(1011),
    FinancialUnStakeTrx(1012),
    FinancialVoteTrx(1013),
    FinancialWithDrawTrx(1014),
    FinancialVoteReward(1015),
    FinancialStakeAndVote(1016),
    FinancialCancelVote(1017),
    //bttc
    BttcDeposit(1021),
    BttcWithdraw(1022),
    BttcRedeem(1023),
    BttcClaimReward(1024),
    //sTrx
    STrxStake(1031),
    STrxUnStake(1032),
    STrxWithdraw(1033),
    //stUsdt
    STUsdtStake(1041),
    STUsdtUnStake(1042),
    STUsdtWithdraw(1043);



    ActionType(int actionType) {
        this.actionType = actionType;
    }
    public static ActionType fromInt(int actionType) {
        for (ActionType t : values()) {
            if (t.actionType == actionType) return t;
        }
        return ContractType;
    }
    private int actionType = 0;

    public int getActionType() {
        return actionType;
    }
}

