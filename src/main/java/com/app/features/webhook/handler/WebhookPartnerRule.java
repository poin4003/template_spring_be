package com.app.features.webhook.handler;

import com.app.features.webhook.entity.WebhookEventEntity;
import com.app.features.webhook.entity.WebhookSubscriptionEntity;
import com.app.features.webhook.enums.WebhookDecision;
import com.app.features.webhook.http.WebhookHttpResponse;
import com.app.features.webhook.worker.WebhookRuleResult;

public interface WebhookPartnerRule {

    String getPartnerCode();

    WebhookDecision evaluate(WebhookHttpResponse<?> response);

    WebhookRuleResult execute(WebhookEventEntity event, WebhookSubscriptionEntity subscription);
}
