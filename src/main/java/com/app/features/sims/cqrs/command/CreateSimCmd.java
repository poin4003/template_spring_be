package com.app.features.sims.cqrs.command;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.app.core.annotation.RegisterMqType;
import com.app.core.exception.ExceptionFactory;
import com.app.features.sims.cqrs.result.SimResult;
import com.app.features.sims.entity.SimEntity;
import com.app.features.sims.enums.SimStatusEnum;
import com.app.features.sims.repository.SimRepsitory;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;

@RegisterMqType("CreateSimCmd")
public record CreateSimCmd(
        String phoneNumber,
        Integer importPrice,
        Integer sellingPrice,
        Integer dealerPrice,
        SimStatusEnum status) implements Command<SimResult> {
}

@Component
@RequiredArgsConstructor
class CreateSimHandler implements Command.Handler<CreateSimCmd, SimResult> {

    private SimRepsitory simRepo;
    private ModelMapper modelMapper;

    @Override
    public SimResult handle(CreateSimCmd cmd) {
        if (simRepo.existsByPhoneNumber(cmd.phoneNumber())) {
            throw ExceptionFactory.dataAlreadyExists("PhoneNumber " + cmd.phoneNumber());
        }

        SimEntity sim = new SimEntity();
        sim.setPhoneNumber(cmd.phoneNumber());
        sim.setImportPrice(cmd.importPrice());
        sim.setSellingPrice(cmd.sellingPrice());
        sim.setDealerPrice(cmd.dealerPrice());
        sim.setStatus(cmd.status());

        simRepo.save(sim);

        return modelMapper.map(sim, SimResult.class);
    }
}
