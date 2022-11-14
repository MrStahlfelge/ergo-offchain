package org.ergoplatform.offchain

import org.ergoplatform.appkit.*
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.time.Instant
import java.time.LocalDate
import java.util.*

class OffchainPlaygoundApplicationTests {

    fun compileContractAndPrintAddress(contractFile: String, constants: Constants): ErgoContract? {
        val classPathResource = ClassPathResource(contractFile)

        val contractSource = classPathResource.inputStream.bufferedReader().use {
            it.readText()
        }

        val contractCompiled =
            ColdErgoClient(NetworkType.MAINNET, Parameters.ColdClientMaxBlockCost).execute { ctx ->
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
}
