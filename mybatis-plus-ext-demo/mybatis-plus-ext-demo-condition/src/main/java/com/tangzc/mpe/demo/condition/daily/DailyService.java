package com.tangzc.mpe.demo.condition.daily;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("daily")
public class DailyService {

    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private Daily2Repository daily2Repository;

    @GetMapping("list")
    public List<Daily> list(String id) {
        return dailyRepository.list();
    }

    @GetMapping("page")
    public Page<Daily> page(String id, Integer pageNum, Integer pageSize) {
        Page<Daily> page = new Page<>(pageNum, pageSize);
        return dailyRepository.page(page);
    }

    @GetMapping("list2")
    public List<Daily2> list2() {
        return daily2Repository.list();
    }

    @GetMapping("/add/daily")
    public void addDaily(String content) {
        dailyRepository.save(new Daily().setContent(content));
    }

    @GetMapping("/add/daily2")
    public void addDaily2(String content) {
        daily2Repository.save(new Daily2().setContent(content));
    }
}
