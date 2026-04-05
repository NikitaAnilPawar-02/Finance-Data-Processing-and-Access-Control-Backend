package com.financesystem.finance.service;

import com.financesystem.finance.dto.EntryDTO;
import com.financesystem.finance.entity.Entry;
import com.financesystem.finance.entity.User;
import com.financesystem.finance.repository.EntryRepository;
import com.financesystem.finance.entity.Type;
import com.financesystem.finance.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntryServiceImpl implements EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Override
    public void createEntry(EntryDTO dto, User user) {
        if (dto.getDate() == null) {
            throw new BadRequestException("Date is required");
        }
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future");
        }
        Entry entry = new Entry();
        entry.setAmount(dto.getAmount());
        entry.setCategory(dto.getCategory());
        entry.setNotes(dto.getNotes());
        entry.setType(dto.getType());
        entry.setDate(dto.getDate());
        entry.setCreatedBy(user);
        entryRepository.save(entry);
    }

    @Override
    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    @Override
    public void updateEntry(Long id, EntryDTO dto) {
        Entry entry = entryRepository.findById(id).orElse(null);
        if (entry == null) {
            throw new ResourceNotFoundException("Entry not found with id: " + id);
        }
        if (dto.getDate() == null) {
            throw new BadRequestException("Date is required");
        }
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future");
        }
        entry.setAmount(dto.getAmount());
        entry.setCategory(dto.getCategory());
        entry.setNotes(dto.getNotes());
        entry.setType(dto.getType());
        entry.setDate(dto.getDate());
        entryRepository.save(entry);
    }

    @Override
    public void deleteEntry(Long id) {
        Entry entry = entryRepository.findById(id).orElse(null);
        if (entry == null) {
            throw new ResourceNotFoundException("Entry not found with id: " + id);
        }
        entryRepository.delete(entry);
    }

    @Override
    public List<Entry> getByType(String type) {
        Type entryType;
        try {
            entryType = Type.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid type. Use INCOME or EXPENSE");
        }
        return entryRepository.findByType(entryType);
    }

    @Override
    public List<Entry> getByCategory(String category) {
        return entryRepository.findByCategory(category);
    }

    @Override
    public double getTotalIncome() {
        Double result = entryRepository.getTotalIncome();
        return result != null ? result : 0;
    }

    @Override
    public double getTotalExpense() {
        Double result = entryRepository.getTotalExpense();
        return result != null ? result : 0;
    }

    @Override
    public double getNetBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    @Override
    public Map<String, Double> getCategorySummary() {
        List<Object[]> results = entryRepository.getCategoryTotals();
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            String category = (String) row[0];
            Double total = (Double) row[1];
            if (total == null) {
                total = 0.0;
            }
            map.put(category, total);
        }
        return map;
    }

    @Override
    public List<Entry> getByDate(LocalDate date) {
        return entryRepository.findByDate(date);
    }

    @Override
    public List<Entry> getByDateRange(LocalDate start, LocalDate end) {
        return entryRepository.findByDateBetween(start, end);
    }

    @Override
    public Map<String, Double> getMonthlySummary(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Entry> entries = getByDateRange(startDate, endDate);
        double income = 0;
        double expense = 0;
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (e.getType() == Type.INCOME) {
                income += e.getAmount();
            } else if (e.getType() == Type.EXPENSE) {
                expense += e.getAmount();
            }
        }
        Map<String, Double> map = new HashMap<>();
        map.put("income", income);
        map.put("expense", expense);
        map.put("netBalance", income - expense);
        return map;
    }

    @Override
    public Map<String, Double> getYearlySummary(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Entry> entries = getByDateRange(startDate, endDate);
        double income = 0;
        double expense = 0;
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (e.getType() == Type.INCOME) {
                income += e.getAmount();
            } else if (e.getType() == Type.EXPENSE) {
                expense += e.getAmount();
            }
        }
        Map<String, Double> map = new HashMap<>();
        map.put("income", income);
        map.put("expense", expense);
        map.put("netBalance", income - expense);
        return map;
    }

    @Override
    public List<Entry> getRecentEntries(int limit) {
        List<Entry> allEntries = entryRepository.findAll();
        for (int i = 0; i < allEntries.size(); i++) {
            for (int j = i + 1; j < allEntries.size(); j++) {
                LocalDate d1 = allEntries.get(i).getDate();
                LocalDate d2 = allEntries.get(j).getDate();
                if (d1.isBefore(d2)) {
                    Entry temp = allEntries.get(i);
                    allEntries.set(i, allEntries.get(j));
                    allEntries.set(j, temp);
                }
            }
        }
        List<Entry> recent = new java.util.ArrayList<>();
        for (int i = 0; i < allEntries.size() && i < limit; i++) {
            recent.add(allEntries.get(i));
        }
        return recent;
    }

    @Override
    public Page<Entry> getPagedEntries(Pageable pageable) {
        return entryRepository.findAll(pageable);
    }
}