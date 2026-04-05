package com.financesystem.finance.service;

import com.financesystem.finance.dto.EntryDTO;
import com.financesystem.finance.entity.Entry;
import com.financesystem.finance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EntryService {

    void createEntry(EntryDTO dto, User user);

    List<Entry> getAllEntries();

    void updateEntry(Long id, EntryDTO dto);

    void deleteEntry(Long id);

    List<Entry> getByType(String type);

    List<Entry> getByCategory(String category);

    double getTotalIncome();

    double getTotalExpense();

    double getNetBalance();

    Map<String, Double> getCategorySummary();

    List<Entry> getByDate(LocalDate date);

    List<Entry> getByDateRange(LocalDate start, LocalDate end);

    Map<String, Double> getMonthlySummary(int year, int month);

    Map<String, Double> getYearlySummary(int year);

    List<Entry> getRecentEntries(int limit);

    Page<Entry> getPagedEntries(Pageable pageable);
}