
#### 可以通过脚本直接调用Pancake的合约进行swap，目前仅支持 有兑换池子 的swap，如果通过proxy多路径兑换，需要依赖第三方（swap wallet api）

#### 可以替换 [main.properties] BSC节点
    http://221.236.31.164:8545

#### 请求参数
        args[0] // fromToken
        args[1] // toToken
        args[2] // 兑换数量
        args[3] // 滑点，10000基数， 1%滑点参数传 100
        args[4] // 私钥

    0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0x0000000000000000000000000000000000000000  0.001 100 {{私钥}}


#### goerli 测试网
    1.ETH兑换其他token
    0x0000000000000000000000000000000000000000 0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0.001  1000 {{私钥}}

    2.其他token兑换ETH
    0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0x0000000000000000000000000000000000000000 1  1000 {{私钥}}

#### BSC主网
    1.BNB兑换USDT
    0x0000000000000000000000000000000000000000 0x55d398326f99059fF775485246999027B3197955  0.01  1000 {{私钥}}

    2.USDT兑换BNB
    0x55d398326f99059fF775485246999027B3197955 0x0000000000000000000000000000000000000000 1 1000 {{私钥}}

    3.USDT兑换PEPE
    0x55d398326f99059fF775485246999027B3197955 0x25d887Ce7a35172C62FeBFD67a1856F20FaEbB00 1 1000 {{私钥}}

