package com.example.customer_api.service;

import java.util.List;

import com.example.customer_api.dto.CustomerRequestDTO;
import com.example.customer_api.dto.CustomerResponseDTO;
import com.example.customer_api.entity.enums.CustomerStatus;

public interface CustomerService {

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);

    void deleteCustomer(Long id);

    List<CustomerResponseDTO> searchCustomers(String keyword);

    List<CustomerResponseDTO> getCustomersByStatus(CustomerStatus status);
}
