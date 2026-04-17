# tronlink-android-core

TronLink Wallet is a decentralized non-custodial wallet.TronLink-Core is the core module of TronLink Wallet, which provides core functions such as Create Wallet, Get Address and Sign Transaction.

## Analytics Module (Privacy-First Design)

The `metrics` module collects aggregated usage data only. **Wallet addresses, keys, mnemonics, transaction hashes, counter-parties, and device identifiers are never transmitted.** The backend receives only opaque UUIDs and same-day aggregated buckets; individual transactions are not reconstructible.

### Address → UUID Anonymization

Each address is mapped to a random UUID stored **locally only**. The backend never sees the address, and the same user's two wallets appear as two independent UUIDs (no server-side linkage).

```java
// UIDMapRepository.java — local-only address → uuid mapping
public synchronized String queryUIDByAddress(String address) {
    UIdMappingEntity entity = uidMappingDao.getByAddress(address);
    if (entity == null) {
        String uuid = newUID();              // UUID.randomUUID().toString()
        insert(address, uuid);                // persisted on-device only
        return uuid;
    }
    return entity.getUId();
}
```

### What Is Uploaded

Records are merged locally per `(uid, actionType, tokenAddress, day)` before upload, and raw amounts are replaced with a 9-bucket logarithmic histogram (`A1`..`A9`). And the payloads are sent encrypted.


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

## Integrity Check

tronlink-android-core.aar is signed by the gpg key as below. You can use the gpg public key to verify the integrity of the officially released core library.

  ```
  pub: 7B910EA80207596075E6D7BA5D34F7A6550473BA
  uid: build_tronlink <build@tronlink.org>
  ```

 For example:

  ```
  #gpg --verify tronlink-android-core-xxx.aar.asc tronlink-android-core-xxx.aar
  gpg: Signature made 一  7/29 16:03:14 2024 CST
  gpg:                using RSA key 7B910EA80207596075E6D7BA5D34F7A6550473BA
  gpg: Good signature from "build_tronlink <build@tronlink.org>"
  ```

## Demo

- [Create new wallet](./core/src/test/java/org/tron/WalletCoreUnitTest.java)
- [Sign transaction](./core/src/test/java/org/tron/TransactionCoreUnitTest.java)
- [Sign message](./core/src/test/java/org/tron/TransactionCoreUnitTest.java)
