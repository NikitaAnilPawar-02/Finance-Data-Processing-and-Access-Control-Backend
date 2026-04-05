package com.financesystem.finance.repository;

import com.financesystem.finance.entity.Entry;
import com.financesystem.finance.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByType(Type type);

    List<Entry> findByCategory(String category);

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.type = 'INCOME'")
    Double getTotalIncome();

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.type = 'EXPENSE'")
    Double getTotalExpense();

    @Query("SELECT e.category, SUM(e.amount) FROM Entry e GROUP BY e.category")
    List<Object[]> getCategoryTotals();

    List<Entry> findByDate(LocalDate date);

    List<Entry> findByDateBetween(LocalDate start, LocalDate end);
}