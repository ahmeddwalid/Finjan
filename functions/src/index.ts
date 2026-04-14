import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import Stripe from "stripe";

admin.initializeApp();

// Initialize Stripe with your secret key from Firebase config
// Set it with: firebase functions:config:set stripe.secret_key="sk_test_..."
const stripe = new Stripe(functions.config().stripe.secret_key, {
  apiVersion: "2024-04-10",
});

/**
 * Creates a Stripe PaymentIntent and returns the client secret.
 * Called from the Android app via Firebase Cloud Functions SDK.
 *
 * Expected data: { amount: number (in cents), currency: string }
 * Returns: { clientSecret: string, paymentIntentId: string }
 */
export const createPaymentIntent = functions.https.onCall(
  async (data, context) => {
    // Verify authentication
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "You must be signed in to make a payment."
      );
    }

    const {amount, currency} = data;

    // Validate input
    if (!amount || typeof amount !== "number" || amount < 50) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Amount must be at least 50 cents."
      );
    }

    if (!currency || typeof currency !== "string") {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Currency is required."
      );
    }

    try {
      const paymentIntent = await stripe.paymentIntents.create({
        amount: Math.round(amount),
        currency: currency.toLowerCase(),
        metadata: {
          userId: context.auth.uid,
        },
      });

      functions.logger.info(
        `PaymentIntent created: ${paymentIntent.id} for user ${context.auth.uid}`
      );

      return {
        clientSecret: paymentIntent.client_secret,
        paymentIntentId: paymentIntent.id,
      };
    } catch (error) {
      functions.logger.error("Error creating PaymentIntent:", error);
      throw new functions.https.HttpsError(
        "internal",
        "Unable to create payment. Please try again."
      );
    }
  }
);
