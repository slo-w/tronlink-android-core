package org.tron;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tron.common.utils.LogUtils;
import org.tron.config.Parameter;
import org.tron.net.CipherException;
import org.tron.walletserver.DuplicateNameException;
import org.tron.walletserver.I_TYPE;
import org.tron.walletserver.InvalidNameException;
import org.tron.walletserver.InvalidPasswordException;
import org.tron.walletserver.Wallet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WalletCoreUnitTest {
    private static final String WALLET_NAME = "walletTest";
    private static final String PRIVETE_KEY = "8e0de1652bb518de63f8a1b8b6d3ad195c069024abe59e4b31fc8a70e124879a";
    private static final String WALLET_ADDRESS = "TLVoXnzfyDmwRDwHwS1yZANjjDWFJ3HwrB";

    @Test
    public void createWallet() throws CipherException, InvalidPasswordException, DuplicateNameException, InvalidNameException {
        Wallet testWallet = new Wallet(true);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_CREATE_WALLET);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println("Address = " + testWallet.getAddress());
        Assert.assertTrue(testWallet.getAddress() != null);
    }


    @Test
    public void importWalletWithPrivateKey() {
        Wallet testWallet = new Wallet(I_TYPE.PRIVATE, PRIVETE_KEY);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_IMPORT_PRIKEY);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println("Address = " + testWallet.getAddress());
        Assert.assertTrue(testWallet.getAddress() != null);
        Assert.assertEquals(WALLET_ADDRESS, testWallet.getAddress());
    }


}
