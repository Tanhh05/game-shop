
import api from "@/api/axios"

export const createPaypalOrder = (amount) => {
    return api.post("/paypal/create-order", {
        amount
    })
}

export const capturePaypalOrder = (orderId) => {
    return api.post("/paypal/capture", {
        orderId: orderId
    })

}