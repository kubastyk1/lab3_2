package lab3_2;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import edu.iis.mto.staticmock.Configuration;
import edu.iis.mto.staticmock.ConfigurationLoader;
import edu.iis.mto.staticmock.IncomingInfo;
import edu.iis.mto.staticmock.IncomingNews;
import edu.iis.mto.staticmock.NewsLoader;
import edu.iis.mto.staticmock.NewsReaderFactory;
import edu.iis.mto.staticmock.PublishableNews;
import edu.iis.mto.staticmock.SubsciptionType;
import edu.iis.mto.staticmock.reader.NewsReader;


@RunWith(PowerMockRunner.class)
@PrepareForTest( {ConfigurationLoader.class, NewsReaderFactory.class, PublishableNews.class} )
public class NewsLoaderTest {

	private NewsLoader newsLoader;
	private IncomingInfo pubInfo = new IncomingInfo("public", SubsciptionType.NONE);
	private IncomingInfo subInfo = new IncomingInfo("subscription", SubsciptionType.A);
	private String readerType = "readerType";
	private List<String> publicContent;
	private List<String> subscribentContent;

	@Before
	public void setUp(){

		newsLoader = new NewsLoader();

		mockStatic(ConfigurationLoader.class);
		ConfigurationLoader configurationLoader = mock(ConfigurationLoader.class);
		when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);

		Configuration configuration = new Configuration();
		Whitebox.setInternalState(configuration, "readerType", readerType);
		when(configurationLoader.loadConfiguration()).thenReturn(configuration);

		IncomingNews incomingNews = new IncomingNews();
		incomingNews.add(pubInfo);
		incomingNews.add(subInfo);

		NewsReader newsReader = mock(NewsReader.class);
		when(newsReader.read()).thenReturn(incomingNews);

		mockStatic(NewsReaderFactory.class);
		NewsReaderFactory newsReaderFactory = new NewsReaderFactory();
		when(newsReaderFactory.getReader(readerType)).thenReturn(newsReader);

		PublishableNews publishableNews = newsLoader.loadNews();
        publicContent = Whitebox.getInternalState(publishableNews, "publicContent");
        subscribentContent = Whitebox.getInternalState(publishableNews, "subscribentContent");
	}

    @Test
    public void publishableNews_publicContentTest() {

        assertThat(publicContent.size(), is(1));
        assertThat(publicContent, hasItem(pubInfo.getContent()));
        assertThat(publicContent, not(hasItem(subInfo.getContent())));
        assertThat(publicContent.get(0), is(equalTo("public")));
    }

    @Test
    public void publishableNews_subscriptionContentTest() {

        assertThat(subscribentContent.size(), is(1));
        assertThat(subscribentContent, hasItem(subInfo.getContent()));
        assertThat(subscribentContent, not(hasItem(pubInfo.getContent())));
        assertThat(subscribentContent.get(0), is(equalTo("subscription")));
    }

    @Test
    public void publishableNews_createCalledOneTime() {

        verifyStatic(times(1));
        PublishableNews.create();
    }
}
