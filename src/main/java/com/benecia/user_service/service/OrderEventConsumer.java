package com.benecia.user_service.service;

import com.benecia.user_service.common.AppException;
import com.benecia.user_service.dto.OrderCreated;
import com.benecia.user_service.dto.PointsFailed;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final UserWriter userWriter;
    private final StreamBridge streamBridge;

    @Bean
    public Consumer<OrderCreated> orderCreated() {
        return orderDto -> {
            log.info("Received order-created event: {}", orderDto);

            try {
                userWriter.addPoints(orderDto.userId(), orderDto.totalPrice());
                log.info("Successfully processed points for user: {}", orderDto.userId());
            } catch (AppException e) { // UserWriter가 던진 AppException을 캐치
                log.error("Failed to process points for userId: {}. Reason: {}", orderDto.userId(), e.getMessage());

                // SAGA - 보상 트랜잭션 이벤트(포인트 적립 실패)를 발행
                PointsFailed failedDto = new PointsFailed(
                        orderDto.orderId(),
                        orderDto.userId(),
                        e.getMessage()
                );

                streamBridge.send("pointsFailed-out-0", failedDto);
                log.info("Published points-failed event for orderId: {}", orderDto.orderId());
            } catch (Exception e) { // 7. 그 외 알 수 없는 예외 처리
                log.error("Unexpected error processing order-created event for userId: {}", orderDto.userId(), e);
                PointsFailed failedDto = new PointsFailed(
                        orderDto.orderId(),
                        orderDto.userId(),
                        "Unexpected error: " + e.getMessage()
                );
                streamBridge.send("pointsFailed-out-0", failedDto);
                log.info("Published points-failed (unexpected) event for orderId: {}", orderDto.orderId());
            }
        };
    }
}
