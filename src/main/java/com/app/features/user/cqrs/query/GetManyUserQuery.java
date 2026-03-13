package com.app.features.user.cqrs.query;

import com.app.features.user.cqrs.result.UserResult;
import com.app.features.user.entity.UserBaseEntity;
import com.app.features.user.repository.UserBaseRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.BaseQuery;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import an.awesome.pipelinr.Command;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetManyUserQuery extends BaseQuery implements Command<Page<UserResult>> {
}

@Component
@RequiredArgsConstructor
class GetManyUserHandler implements Command.Handler<GetManyUserQuery, Page<UserResult>> {

    private final UserBaseRepository userBaseRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResult> handle(GetManyUserQuery query) {
        Pageable pageable = PageRequest.of(query.getCurrentPage() - 1, query.getPageSize());

        Page<UserBaseEntity> entityPage = userBaseRepo.findAll(pageable);

        return entityPage.map(result -> modelMapper.map(result, UserResult.class));
    }
}
