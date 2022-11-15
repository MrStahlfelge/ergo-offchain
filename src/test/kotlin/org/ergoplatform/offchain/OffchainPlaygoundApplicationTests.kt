package org.ergoplatform.offchain

import org.ergoplatform.appkit.*
import org.ergoplatform.appkit.impl.Eip4TokenBuilder
import org.ergoplatform.appkit.impl.NodeAndExplorerDataSourceImpl
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.util.*

class OffchainPlaygoundApplicationTests {

    fun compileContractAndPrintAddress(contractFile: String, constants: Constants): ErgoContract? {
        val classPathResource = ClassPathResource(contractFile)

        val contractSource = classPathResource.inputStream.bufferedReader().use {
            it.readText()
        }

        val contractCompiled =
            ColdErgoClient(
                NetworkType.MAINNET,
                Parameters.ColdClientMaxBlockCost,
                Parameters.ColdClientBlockVersion
            ).execute { ctx ->
                ctx.compileContract(constants, contractSource)
            }

        println("Address: ${contractCompiled.toAddress()}")
        println("Ergotree: ${contractCompiled.ergoTree.bytesHex()}")

        return contractCompiled
    }

    @Test
    fun deadlineContract() {
        compileContractAndPrintAddress(
            "deadline.es",
            ConstantsBuilder.create()
                .item("deadline", Date(2023, 1, 1).time)
                .item(
                    "publicKeyAlice",
                    Address.create("9ewA9T53dy5qvAkcR5jVCtbaDW2XgWzbLPs5H4uCJJavmA4fzDx").publicKey
                )
                .item(
                    "publicKeyBob",
                    Address.create("9i6UmaoJKWHgWkuq1EJUoYu2hrkRkxAYwQjDotHRHfGrBo16Rss").publicKey
                )
                .build()
        )
    }

    @Test
    fun sendTx() {
        RestApiErgoClient.create(
            "https://node.ergo.watch/",
            NetworkType.MAINNET,
            "",
            RestApiErgoClient.defaultMainnetExplorerUrl
        ).execute { ctx ->

            val prover = ctx.newProverBuilder()
                .withMnemonic(SecretString.create(""), SecretString.empty(), false)
                .withEip3Secret(0)
                .build()

            println(prover.eip3Addresses.first())

            //BoxOperations.createForSender(Address.create("9i6UmaoJKWHgWkuq1EJUoYu2hrkRkxAYwQjDotHRHfGrBo16Rss"), ctx)

            val boxOperations = BoxOperations.createForEip3Prover(prover, ctx)

            boxOperations
                .withAmountToSpend(Parameters.OneErg)
                //.withTokensToSpend(listOf(ErgoToken("bc01920a596a714d5a87d7ddf18f2fc9e1cfc9b21f52d90d1423d1f13fca4ba1", 1)))
                .withInputBoxesLoader(ExplorerAndPoolUnspentBoxesLoader().withAllowChainedTx(true))
                .send(Address.create("9i6UmaoJKWHgWkuq1EJUoYu2hrkRkxAYwQjDotHRHfGrBo16Rss"))

            //boxOperations.mintTokenToContractTxUnsigned(address, tokenBuilder = { id ->
            //    Eip4Token(id, 1000, "Test token", "-", 0)
            //})

            boxOperations.buildTxWithDefaultInputs { txB ->
                val output = txB.outBoxBuilder()
                    .value(Parameters.OneErg)
                    .registers(ErgoValue.of(1))
                    .build()

                txB.outputs(output)

                txB
            }

            // Explorer API:
            // (ctx.dataSource as NodeAndExplorerDataSourceImpl).explorerApi.getApiV1AddressesP1BalanceTotal()
        }
    }
}
