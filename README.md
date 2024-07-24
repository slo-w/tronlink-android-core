# tronlink-android-core

TronLink Wallet is a decentralized non-custodial wallet.TronLink-Core is the core module of TronLink Wallet, which provides core functions such as Create Wallet, Get Address, and Sign Transaction.

## Requirements

- Android 21+
- Java 1.8+

## How to use
Add the JitPack maven repository.
```
maven { url "https://jitpack.io"  }
```
Add dependency.
```
implementation 'com.github.TronLink:tronlink-android-core:1.0.2@aar'
```

## Demo

- [Create new wallet](./core/src/test/java/org/tron/WalletCoreUnitTest.java)
- [Sign transaction](./core/src/test/java/org/tron/TransactionCoreUnitTest.java)
- [Sign message](./core/src/test/java/org/tron/TransactionCoreUnitTest.java)
