package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by Patryk Wierzyński
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest( {ConfigurationLoader.class, NewsReaderFactory.class, PublishableNews.class} )

public class NewsLoaderTest {
	private NewsLoader newsLoader;
	private String readerType;

	private IncomingInfo infoPublic = new IncomingInfo("public", SubsciptionType.NONE);
	private IncomingInfo infoSubA = new IncomingInfo("subscription A", SubsciptionType.A);
	private IncomingInfo infoSubB = new IncomingInfo("subscription B", SubsciptionType.B);
	private IncomingInfo infoSubC = new IncomingInfo("subscription C", SubsciptionType.C);


	@Before
	public void setUp() throws Exception {
		newsLoader = new NewsLoader();
		readerType = "testReader";

		mockStatic(ConfigurationLoader.class);
		ConfigurationLoader mockLoader = mock(ConfigurationLoader.class);
		when(ConfigurationLoader.getInstance()).thenReturn(mockLoader);

		Configuration configuration = new Configuration();
		Whitebox.setInternalState(configuration, "readerType", readerType);
		when(mockLoader.loadConfiguration()).thenReturn(configuration);

		IncomingNews news = new IncomingNews();
		news.add(infoPublic);
		news.add(infoSubA);
		news.add(infoSubB);
		news.add(infoSubC);

		NewsReader mockReader = mock(NewsReader.class);
		when(mockReader.read()).thenReturn(news);

		mockStatic(NewsReaderFactory.class);
		when(NewsReaderFactory.getReader(readerType)).thenReturn(mockReader);

		mockStatic(PublishableNews.class);
		when(PublishableNews.create()).thenReturn(new PublishableNewsTester());
	}

	@Test
	public void loadNews_publicContentHasOnlyPublicNews() throws Exception {
		PublishableNewsTester news = (PublishableNewsTester) newsLoader.loadNews();

		assertThat(news.getPublicContent(), hasItem(infoPublic.getContent()));
		assertThat(news.getPublicContent(), not(hasItem(infoSubA.getContent())));
		assertThat(news.getPublicContent(), not(hasItem(infoSubB.getContent())));
		assertThat(news.getPublicContent(), not(hasItem(infoSubC.getContent())));
	}

	@Test
	public void loadNews_subscribentContentHasOnlySubscribedNews() throws Exception {
		PublishableNewsTester news = (PublishableNewsTester) newsLoader.loadNews();

		assertThat(news.getSubscribentContent(), not(hasItem(infoPublic.getContent())));
		assertThat(news.getSubscribentContent(), hasItem(infoSubA.getContent()));
		assertThat(news.getSubscribentContent(), hasItem(infoSubB.getContent()));
		assertThat(news.getSubscribentContent(), hasItem(infoSubC.getContent()));
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