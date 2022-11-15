package org.ergoplatform.offchain

import org.ergoplatform.appkit.*
import org.ergoplatform.ergopay.ErgoPayResponse
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@CrossOrigin
class ErgoPayController {

    @GetMapping("/ergopaytest")
    fun replyWithMessage(): ErgoPayResponse {

        return ErgoPayResponse().apply {
            //message = "Ein erster Test"
            //messageSeverity = ErgoPayResponse.Severity.WARNING

            RestApiErgoClient.create(
                "https://node.ergo.watch/",
                NetworkType.MAINNET,
                "",
                RestApiErgoClient.defaultMainnetExplorerUrl
            ).execute { ctx ->

                val unsignedTx = BoxOperations.createForSender(
                    Address.create("9ewA9T53dy5qvAkcR5jVCtbaDW2XgWzbLPs5H4uCJJavmA4fzDx"),
                    ctx
                )
                    .withAmountToSpend(Parameters.OneErg)
                    .putToContractTxUnsigned(
                        Address.create("9ewA9T53dy5qvAkcR5jVCtbaDW2XgWzbLPs5H4uCJJavmA4fzDx")
                            .toErgoContract()
                    )

                reducedTx = Base64.getUrlEncoder()
                    .encodeToString(ctx.newProverBuilder().build().reduce(unsignedTx, 0).toBytes())
                message = "1 ERG round trip"

            }
        }

    }

}