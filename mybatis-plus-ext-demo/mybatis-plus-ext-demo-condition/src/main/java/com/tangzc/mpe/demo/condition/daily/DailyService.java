package com.tangzc.mpe.demo.condition.daily;

import com.tangzc.mpe.demo.condition.daily2.Daily2;
import com.tangzc.mpe.demo.condition.daily2.DailyRepository2;
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
    @Resource
    private DailyRepository2 dailyRepository2;

    @GetMapping("list")
    public List<Daily> list(String id) {
        return dailyRepository.list();
    }

    @GetMapping("list2")
    public List<Daily2> list2() {
        return dailyRepository2.list();
    }
}
