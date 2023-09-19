package com.tangzc.mpe.demo.bind.daily;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class DailyService {

    @Resource
    private DailyRepository dailyRepository;

    @GetMapping("bind/list")
    public List<Daily> list(){
        return dailyRepository.lambdaQueryPlus().bindList();
    }

    @GetMapping("bind/saveBatch")
    public List<Daily> saveBatch(){
        Daily daily = new Daily();
        daily.setContent(LocalDateTime.now().toString());
        Daily daily2 = new Daily();
        daily2.setContent(LocalDateTime.now().toString());
        dailyRepository.saveBatch(Arrays.asList(daily, daily2));
        return dailyRepository.lambdaQueryPlus().bindList();
    }
}
