package com.tangzc.mpe.demo.condition.daily;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("daily")
public class DailyService {

    @Resource
    private DailyRepository dailyRepository;

    @GetMapping("list")
    public List<Daily> list(String id) {
        return dailyRepository.list();
    }
}
