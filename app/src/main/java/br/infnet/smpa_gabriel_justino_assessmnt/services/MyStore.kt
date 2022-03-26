package br.infnet.smpa_gabriel_justino_assessmnt.services

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MyProduct(
    val sku: String,
    var description: String?,
    var price: String?,
    var skuDetails: SkuDetails?
)

class MyStore : BillingClientStateListener,
    PurchasesUpdatedListener, SkuDetailsResponseListener {

    val myProducts = mutableListOf<MyProduct>()

    constructor(context: AppCompatActivity) {
        this.context = context
        myProducts.add(
            MyProduct(
                "android.test.purchased",
                null, null, null
            )
        )
        myBillingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        myBillingClient.startConnection(this)
    }

    private lateinit var myBillingClient: BillingClient
    private lateinit var context: AppCompatActivity
    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        //
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            println("BILLING: conexao estabelecida")
        }
        val skuList = ArrayList<String>()
        for (itemIndex in 0..myProducts.size - 1) {
            skuList.add(myProducts.get(itemIndex).sku)
        }
        val skusParams = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)

        myBillingClient.querySkuDetailsAsync(skusParams.build(), this)
    }

    override fun onBillingServiceDisconnected() {
        //
    }

    fun makePurchase(products: MyProduct) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(products.skuDetails)
            .build()
        myBillingClient.launchBillingFlow(context, flowParams)
    }
    private suspend fun handlePurchases(purchase:Purchase){
        when(purchase.purchaseState){
            Purchase.PurchaseState.PURCHASED -> {
                if(!purchase.isAcknowledged){
                    val purchaseParams = AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    val resultPurchase = withContext(Dispatchers.IO){
                        myBillingClient.acknowledgePurchase(purchaseParams)
                        print("acknowledgePurchase")
                    }
                }

            }

        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchaseList: MutableList<Purchase>?
    ) {
        if (!purchaseList.isNullOrEmpty()) {
            when (billingResult?.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    for (itemIndex in 0..purchaseList.size - 1) {
                        GlobalScope.launch(Dispatchers.IO) {
                            handlePurchases(purchaseList.get(itemIndex))
                        }
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                }
            }
        }


    }

    override fun onSkuDetailsResponse(
        bllingResult: BillingResult?,
        listSkuDetails: MutableList<SkuDetails>?
    ) {
        //
        if (bllingResult?.responseCode == BillingClient.BillingResponseCode.OK
            &&
            !listSkuDetails.isNullOrEmpty()
        ) {
            for (productIndex in 0..myProducts.size - 1) {
                var product = myProducts.get(productIndex)
                setProductIfFoundOnSkuList(listSkuDetails, product)
            }
        }
    }

    private fun setProductIfFoundOnSkuList(
        listSkuDetails: MutableList<SkuDetails>,
        product: MyProduct
    ) {
        for (skusIndex in 0..listSkuDetails.size - 1) {
            val skuProduct = listSkuDetails.get(skusIndex)
            if (product.sku.equals(skuProduct.sku)) {
                product.description = skuProduct.description
                product.price = skuProduct.price
                product.skuDetails = skuProduct
            }
        }
    }
}