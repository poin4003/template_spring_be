package com.app.features.auth.cqrs.command;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.features.auth.repository.KeyStoreRepository;
import com.app.features.user.entity.UserBaseEntity;
import com.app.features.user.repository.UserBaseRepository;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;

public record LogoutCmd(
    UUID keyStoreId,
    UUID userId
) implements Command<Void> {}

@Component
@RequiredArgsConstructor
class LogoutHandler implements Command.Handler<LogoutCmd, Void> {

    private final UserBaseRepository userBaseRepo;
    private final KeyStoreRepository keyStoreRepo;

    @Override
    public Void handle(LogoutCmd cmd) {
        UUID keyStoreId = Objects.requireNonNull(cmd.keyStoreId(), "Key store id must be not null");

        updateLogoutInfo(cmd.userId());

        keyStoreRepo.deleteById(keyStoreId);

        return null;
    }

    private void updateLogoutInfo(UUID userId) {
        UserBaseEntity user = new UserBaseEntity();
        user.setId(userId);
        user.setLogoutTime(LocalDateTime.now());

        userBaseRepo.save(user);
    }
}
