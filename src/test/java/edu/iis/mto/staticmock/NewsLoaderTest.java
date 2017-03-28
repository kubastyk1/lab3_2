package edu.iis.mto.staticmock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patryk Wierzyński
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest( {ConfigurationLoader.class, NewsReaderFactory.class, PublishableNews.class} )

public class NewsLoaderTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void loadNews() throws Exception {

	}

}

class PublishableNewsTester extends PublishableNews {
	private final List<String> publicContent = new ArrayList<>();
	private final List<String> subscribentContent = new ArrayList<>();

	public List<String> getPublicContent() {
		return publicContent;
	}

	public List<String> getSubscribentContent() {
		return subscribentContent;
	}

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