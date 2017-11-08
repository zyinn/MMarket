package com.sumscope.optimus.moneymarket;


import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.websocket.WebSocketManager;
import com.sumscope.optimus.commons.websocket.WebSocketSender;
import com.sumscope.optimus.commons.websocket.handler.SystemWebSocketHandler;
import com.sumscope.optimus.commons.websocket.interceptor.HandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements
        WebSocketConfigurer {
    public static final String DETAILS_WEBSOCKET_SENDER = "Details_Websocket_Sender";
    public static final String MAIN_WEBSOCKET_SENDER = "Main_Websocket_Sender";
    private WebSocketManager detailsQWebSocketSender;
    private WebSocketManager mainQWebSocketSender;

    public WebSocketConfig() {
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(detailsQWebSocketHandler(), "/websck/mm").setAllowedOrigins("*").addInterceptors(new HandshakeInterceptor());
        registry.addHandler(detailsQWebSocketHandler(), "/sockjs/websck/mm").addInterceptors(new HandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();

        registry.addHandler(mainQWebSocketHandler(), "/websck/mmmain").setAllowedOrigins("*").addInterceptors(new HandshakeInterceptor());
        registry.addHandler(mainQWebSocketHandler(), "/sockjs/websck/mmmain").addInterceptors(new HandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
        LogManager.info( "启动时 WebSocket 注册成功!");
    }


    private WebSocketHandler mainQWebSocketHandler() {
        return new SystemWebSocketHandler((WebSocketManager) mainQWebSocketSender());
    }

    @Bean(name = MAIN_WEBSOCKET_SENDER, autowire = Autowire.BY_NAME)
    public WebSocketSender mainQWebSocketSender(){
        if(this.mainQWebSocketSender == null){
            this.mainQWebSocketSender = new WebSocketManager();
        }
        return mainQWebSocketSender;
    }


    private WebSocketHandler detailsQWebSocketHandler() {
        return new SystemWebSocketHandler((WebSocketManager) detailsQWebSocketSender());
    }

    @Bean(name = DETAILS_WEBSOCKET_SENDER, autowire = Autowire.BY_NAME)
    public WebSocketSender detailsQWebSocketSender(){
        if(this.detailsQWebSocketSender == null){
            this.detailsQWebSocketSender = new WebSocketManager();
        }
        return detailsQWebSocketSender;
    }

}
