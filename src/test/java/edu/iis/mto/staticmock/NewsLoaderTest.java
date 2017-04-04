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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class,NewsReaderFactory.class,PublishableNews.class})

public class NewsLoaderTest {
    private IncomingNews incomingNews =  new IncomingNews();
    private String testReaderType = "WS";
    private IncomingInfo publicInfo = new IncomingInfo("PublicInfo", SubsciptionType.NONE);
    private IncomingInfo subInfo = new IncomingInfo("subInfo", SubsciptionType.B);
    private NewsLoader newsLoader;




    @Before
    public void setUp() throws Exception {
        newsLoader = new NewsLoader();
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader  configurationMockLoader = mock(ConfigurationLoader.class);
        incomingNews.add(publicInfo);
        incomingNews.add(subInfo);
        mockStatic(NewsReaderFactory.class);
        NewsReader newsReaderMock = new NewsReader() {
            @Override
            public IncomingNews read() {
                return incomingNews;
            }
        };
        Configuration configuration = new Configuration();
        Whitebox.setInternalState(configuration, "readerType", testReaderType);
        when(ConfigurationLoader.getInstance()).thenReturn(configurationMockLoader);
        when(configurationMockLoader.loadConfiguration()).thenReturn(configuration);
        when(NewsReaderFactory.getReader(testReaderType)).thenReturn(newsReaderMock);

    }


    @Test
    public void testNewsLoaderLoadNewsCheckPublicNews(){
        PublishableNews publishableNews = newsLoader.loadNews();
        List<String> result = (List<String>)Whitebox.getInternalState(publishableNews,"publicContent");
        assertThat(result.size(), is(1));
        assertThat(result,not(hasItem(subInfo.getContent())));
    }

    @Test
    public void testNewsLoaderTestLoadNewsCheckSubNews(){
        final PublishableNews publishableNews = getPublishableNewsMockWithOverrideAddForSubMethod();
        mockStatic(PublishableNews.class);
        when(PublishableNews.create()).thenReturn(publishableNews);
        PublishableNews publishable = newsLoader.loadNews();
        List<String> result = (List<String>)Whitebox.getInternalState(publishable,"subscribentContent");
        assertThat(result.size(), is(1));
        assertThat(result.get(0),is(equalTo("subInfo")));
    }

    private PublishableNews getPublishableNewsMockWithOverrideAddForSubMethod() {
        return new PublishableNews(){
            private final List<String> subscribentContent = new ArrayList<>();
            @Override
            public void addForSubscription(String content, SubsciptionType subscriptionType) {
                this.subscribentContent.add(content);
            }
        };
    }


}