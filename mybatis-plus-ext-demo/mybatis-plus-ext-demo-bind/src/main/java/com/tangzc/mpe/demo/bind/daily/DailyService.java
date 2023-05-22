package com.tangzc.mpe.demo.bind.daily;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DailyService {

    @Resource
    private DailyRepository dailyRepository;

    @GetMapping("bind/list")
    public List<Daily> list(){
        return dailyRepository.lambdaQueryPlus().bindList();
    }
}
