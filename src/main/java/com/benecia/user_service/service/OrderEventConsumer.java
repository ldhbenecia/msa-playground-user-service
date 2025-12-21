package com.benecia.user_service.service;

import com.benecia.user_service.common.AppException;
import com.benecia.user_service.event.OrderCancelled;
import com.benecia.user_service.event.OrderCreated;
import com.benecia.user_service.event.PointsFailed;
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
            } catch (AppException e) {
                log.error("Failed to process points for userId: {}. Reason: {}", orderDto.userId(), e.getMessage());

                // SAGA - 보상 트랜잭션 이벤트(포인트 적립 실패)를 발행
                PointsFailed failedDto = new PointsFailed(
                        orderDto.orderId(),
                        orderDto.userId(),
                        e.getMessage()
                );

                streamBridge.send("pointsFailed-out-0", failedDto);
            } catch (Exception e) {
                log.error("Failed to deduct points: {}", e.getMessage());
                PointsFailed failedDto = new PointsFailed(
                        orderDto.orderId(),
                        orderDto.userId(),
                        "Unexpected error: " + e.getMessage()
                );
                streamBridge.send("pointsFailed-out-0", failedDto);
            }
        };
    }

    @Bean
    public Consumer<OrderCancelled> orderCancelled() {
        return cancelledDto -> {
            log.info("Received order-cancelled. Refunding points for userId: {}", cancelledDto.userId());
            try {
                userWriter.refundPoints(cancelledDto.userId(), cancelledDto.totalPrice());
            } catch (Exception e) {
                // 이미 실패해서 포인트가 안 나갔거나 등등. 로그만 남김.
                log.warn("Failed to refund points (might be not deducted): {}", e.getMessage());
            }
        };
    }
}
