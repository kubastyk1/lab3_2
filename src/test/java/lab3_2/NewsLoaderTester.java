package lab3_2;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.iis.mto.staticmock.Configuration;
import edu.iis.mto.staticmock.ConfigurationLoader;
import edu.iis.mto.staticmock.IncomingInfo;
import edu.iis.mto.staticmock.IncomingNews;
import edu.iis.mto.staticmock.NewsLoader;
import edu.iis.mto.staticmock.NewsReaderFactory;
import edu.iis.mto.staticmock.PublishableNews;
import edu.iis.mto.staticmock.SubsciptionType;
import edu.iis.mto.staticmock.reader.FileNewsReader;
import edu.iis.mto.staticmock.reader.NewsReader;

import static org.powermock.api.mockito.PowerMockito.*;
import org.mockito.internal.util.reflection.*;

import static org.hamcrest.CoreMatchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
	ConfigurationLoader.class,
	NewsReaderFactory.class,
	PublishableNews.class
})

public class NewsLoaderTester {
	
	private ConfigurationLoader configurationLoaderMock = null;
	
	@Before
	public void setUpTests() {
		configurationLoaderMock = PowerMockito.mock(ConfigurationLoader.class);
		PowerMockito.mockStatic(ConfigurationLoader.class);
		PowerMockito.when(ConfigurationLoader.getInstance()).thenReturn(configurationLoaderMock);
		
		Configuration configuration = new Configuration();
		Whitebox.setInternalState(configuration, "readerType", "test");
		when(configurationLoaderMock.loadConfiguration()).thenReturn(configuration);
		
		NewsReader newsReader = PowerMockito.mock(FileNewsReader.class);
		IncomingNews incomingNews = new IncomingNews();
		incomingNews.add(new IncomingInfo("public info 1", SubsciptionType.NONE));
		incomingNews.add(new IncomingInfo("public info 2", SubsciptionType.NONE));
		incomingNews.add(new IncomingInfo("subscription info 2", SubsciptionType.A));
		incomingNews.add(new IncomingInfo("subscription info 2", SubsciptionType.B));
		when(newsReader.read()).thenReturn(incomingNews);
		
		PowerMockito.mockStatic(NewsReaderFactory.class);		
		when(NewsReaderFactory.getReader("test")).thenReturn(newsReader);		
	}
	
	@Test
	public void test() {
		NewsLoader newsLoader = new NewsLoader();
		PublishableNews publishableNews = newsLoader.loadNews();
		
		//System.out.println(publishableNews.getPublicContent().toString());
	}
}