package com.greenblat.service;

import com.greenblat.dao.CurrencyDAO;
import com.greenblat.dao.impl.CurrencyDAOImpl;
import com.greenblat.dto.CurrencyDTO;
import com.greenblat.entity.Currency;
import com.greenblat.exception.ResourceAlreadyExistsException;
import com.greenblat.exception.ResourceNotFoundException;
import com.greenblat.mapper.CurrencyMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {

    private final CurrencyMapper currencyMapper;
    private final CurrencyDAO currencyDAO;

    public CurrencyService() {
        this.currencyMapper = new CurrencyMapper();
        this.currencyDAO = new CurrencyDAOImpl();
    }

    public List<CurrencyDTO> getAllCurrencies() throws SQLException {
        return currencyDAO.findAll()
                .stream()
                .map(currencyMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public CurrencyDTO saveCurrency(CurrencyDTO currencyDTO) throws SQLException {
        currencyDAO.findByCode(currencyDTO.code())
                .ifPresent((currency) -> {
                    throw new ResourceAlreadyExistsException("Currency with code [%s] already exists"
                            .formatted(currencyDTO.code()));
                });

        Currency savedCurrency = currencyDAO.save(currencyMapper.mapToCurrency(currencyDTO));
        return currencyMapper.mapToDTO(savedCurrency);
    }

    public CurrencyDTO getCurrencyByCode(String code) throws SQLException {
        return currencyDAO.findByCode(code)
                .map(currencyMapper::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Currency with code [%s] not found".formatted(code)));
    }
}
