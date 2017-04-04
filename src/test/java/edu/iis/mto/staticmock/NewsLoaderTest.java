package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by Sasho on 2017-03-27.
 */
@PrepareForTest( {NewsReaderFactory.class, PublishableNews.class, ConfigurationLoader.class} )
@RunWith(PowerMockRunner.class)
public class NewsLoaderTest {
    private NewsLoader newsLoader;
    private String readerType = "dummyReader";
    private IncomingNews incomingNews = new IncomingNews();
    private IncomingInfo incomingInfoPub = new IncomingInfo("incomingInfoPub", SubsciptionType.NONE);
    private IncomingInfo incomingInfoSubC = new IncomingInfo("incomingInfoSubB", SubsciptionType.C);

    @Before
    public void setUp() throws Exception {
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader mockLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(mockLoader);

        Configuration configuration = new Configuration();
        Whitebox.setInternalState(configuration, "readerType", readerType);
        when(mockLoader.loadConfiguration()).thenReturn(configuration);

        mockStatic(PublishableNews.class);
        when(PublishableNews.create()).thenReturn(new TestablePublishableNews());

        incomingNews = new IncomingNews();
        incomingNews.add(incomingInfoPub);
        incomingNews.add(incomingInfoSubC);

        NewsReader testDataReader = new NewsReader() {
            @Override
            public IncomingNews read() {
                return incomingNews;
            }
        };
        
        mockStatic(NewsReaderFactory.class);
        when(NewsReaderFactory.getReader(readerType)).thenReturn(testDataReader);

        newsLoader = new NewsLoader();
    }

    @Test
    public void test_newsSeparation() throws Exception {
        TestablePublishableNews testablePublishableNews = (TestablePublishableNews) newsLoader.loadNews();

        assertThat(testablePublishableNews.getPublicContent(), hasItem(incomingInfoPub.getContent()));
        assertThat(testablePublishableNews.getSubscribentContent(), not(hasItem(incomingInfoPub.getContent())));
        assertThat(testablePublishableNews.getPublicContent(), not(hasItem(incomingInfoSubC.getContent())));
        assertThat(testablePublishableNews.getSubscribentContent(), hasItem(incomingInfoSubC.getContent()));
    }

    @Test
    public void test_verification() throws Exception {
        newsLoader.loadNews();
        verifyStatic( times(1));
        NewsReaderFactory.getReader(readerType);
    }
}