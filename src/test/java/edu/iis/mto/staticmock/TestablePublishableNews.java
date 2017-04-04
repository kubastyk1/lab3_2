package edu.iis.mto.staticmock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sasho on 2017-03-27.
 */
public class TestablePublishableNews extends PublishableNews {
    private final List<String> publicContent = new ArrayList<>();

    public List<String> getSubscribentContent() {
        return subscribentContent;
    }

    public List<String> getPublicContent() {
        return publicContent;
    }

    private final List<String> subscribentContent = new ArrayList<>();

    @Override
    public void addPublicInfo(String content) {
        super.addPublicInfo(content);
        publicContent.add(content);
    }

    @Override
    public void addForSubscription(String content, SubsciptionType subscriptionType) {
        super.addForSubscription(content, subscriptionType);
        subscribentContent.add(content);
    }
}
