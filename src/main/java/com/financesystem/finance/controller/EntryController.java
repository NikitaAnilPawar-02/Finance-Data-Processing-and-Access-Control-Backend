package com.financesystem.finance.controller;

import com.financesystem.finance.dto.EntryDTO;
import com.financesystem.finance.entity.Entry;
import com.financesystem.finance.entity.User;
import com.financesystem.finance.security.TokenValidator;
import com.financesystem.finance.service.EntryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entries")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @Autowired
    private TokenValidator tokenValidator;

    @PostMapping
    public ResponseEntity<String> createEntry(@Valid @RequestBody EntryDTO dto, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        entryService.createEntry(dto, user);
        return new ResponseEntity<>("Entry Created", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEntry(@PathVariable Long id, @Valid @RequestBody EntryDTO dto, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        entryService.updateEntry(id, dto);
        return new ResponseEntity<>("Entry Updated", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEntry(@PathVariable Long id, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        entryService.deleteEntry(id);
        return new ResponseEntity<>("Entry Deleted", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Entry>> getAllEntries(HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getAllEntries(), HttpStatus.OK);
    }

    @GetMapping("/type")
    public ResponseEntity<List<Entry>> getByType(@RequestParam String type, HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getByType(type), HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<List<Entry>> getByCategory(@RequestParam String category, HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getByCategory(category), HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Entry>> getByDate(@RequestParam String date, HttpServletRequest request) {
        tokenValidator.validate(request);
        LocalDate localDate = LocalDate.parse(date);
        return new ResponseEntity<>(entryService.getByDate(localDate), HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Entry>> getByDateRange(@RequestParam String start, @RequestParam String end, HttpServletRequest request) {
        tokenValidator.validate(request);
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(entryService.getByDateRange(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(HttpServletRequest request) {
        tokenValidator.validate(request);
        Map<String, Double> result = new HashMap<>();
        result.put("totalIncome", entryService.getTotalIncome());
        result.put("totalExpense", entryService.getTotalExpense());
        result.put("netBalance", entryService.getNetBalance());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategorySummary(HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getCategorySummary(), HttpStatus.OK);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<Map<String, Double>> getMonthlySummary(@RequestParam int year, @RequestParam int month, HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getMonthlySummary(year, month), HttpStatus.OK);
    }

    @GetMapping("/yearly-summary")
    public ResponseEntity<Map<String, Double>> getYearlySummary(@RequestParam int year, HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getYearlySummary(year), HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Entry>> getRecentEntries(@RequestParam int limit, HttpServletRequest request) {
        tokenValidator.validate(request);
        return new ResponseEntity<>(entryService.getRecentEntries(limit), HttpStatus.OK);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Entry>> getPagedEntries(@RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        tokenValidator.validate(request);
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(entryService.getPagedEntries(pageable), HttpStatus.OK);
    }
}