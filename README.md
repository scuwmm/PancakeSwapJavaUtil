
#### 可以通过脚本直接调用Pancake的合约进行swap，目前仅支持 有兑换池子 的swap，如果通过proxy多路径兑换，需要依赖第三方（swap wallet api）

#### 请求参数
        args[0] // fromToken
        args[1] // toToken
        args[2] // 兑换数量
        args[3] // 滑点，10000基数， 1%滑点参数传 100
        args[4] // 私钥

    0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0x0000000000000000000000000000000000000000  0.001 100 私钥


#### goerli 测试网
    1.其他token兑换ETH
    0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0x0000000000000000000000000000000000000000 1  1000 私钥
    2.ETH兑换其他token
    0x0000000000000000000000000000000000000000 0x302BaE587Ab9E1667a2d2b0FD67730FEfDD1AB2d 0.001  1000 私钥

