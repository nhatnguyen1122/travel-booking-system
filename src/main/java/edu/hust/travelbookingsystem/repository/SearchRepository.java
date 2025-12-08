package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager; // inject vao de co the tuong tac voi co so du lieu

    // tim kiem theo destination
    public  PageResponse getAllOrderWithSortByMultipleColumsAndSearch(int pageNo, int pageSize, String search, String sortBy){
        StringBuilder sqlQuery = new StringBuilder("select o from Order o where 1=1 ");
        if(StringUtils.hasLength(search)){
            // tim kiem theo destination
            sqlQuery.append(" and lower(o.destination) like lower(:destination) "); // :destination : tham so dong duoc gan sau bang setParameter
        }
        if(StringUtils.hasLength(sortBy)){
            // totalPrice:desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                sqlQuery.append(String.format("order by o.%s %s",matcher.group(1) ,matcher.group(3)));
            }
        }
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        // pageNo : so trang bat dau tim
        // pageSize : moi trang chua toi da pageSize ban ghi
        selectQuery.setFirstResult(pageNo*pageSize); // tim bat dau tu dong pageNo*pageSize
        selectQuery.setMaxResults(pageSize);
        if(StringUtils.hasLength(search)){
            //selectQuery.setParameter("destination", String.format("%%%s%%", search));
            selectQuery.setParameter("destination", "%"+search+"%");
        }
        List<Order> orders = (List<Order>) selectQuery.getResultList();
        System.out.println("orders: " + orders);

        // query ra so item
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from Order o where 1=1 ");
        if(StringUtils.hasLength(search)){
            // tim kiem theo destination
            sqlCountQuery.append(" and lower(o.destination) like lower(:destination) "); // :destination : tham so dong duoc gan sau bang setParameter
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasLength(search)){
            //selectQuery.setParameter("destination", String.format("%%%s%%", search));
            selectCountQuery.setParameter("destination", "%"+search+"%");
        }
        Long totalElements = (Long) selectCountQuery.getSingleResult();
        System.out.println("total elements: " + totalElements);
        // tính total page
        int totalPages = 0 ;
        if(totalElements % pageSize == 0){
            totalPages = (int) (totalElements/pageSize);
        }else {
            totalPages = (int) (totalElements/pageSize) + 1;
        }
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .items(orders)
                .build();
    }
    public PageResponse advanceSearchOrder(int pageNo, int pageSize, String sortBy, String... search){
        return null ;
    }

    public PageResponse findBySearch(int pageNo, int pageSize, String search){
        StringBuilder sqlQuery = new StringBuilder("select u from User u where 1=1 ");
        if(StringUtils.hasLength(search)){
            sqlQuery.append(" and (lower(u.fullName) like lower(:name)" +
                            " or u.phone like :phone "+
                         " or lower(u.email) like lower(:email))");
        }
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        System.out.println("selectQuery: " + selectQuery);
        if(StringUtils.hasLength(search)){
            selectQuery.setParameter("name", "%"+search+"%");
            selectQuery.setParameter("phone", "%"+search+"%");
            selectQuery.setParameter("email", "%"+search+"%");
        }
        selectQuery.setFirstResult(pageNo*pageSize);
        selectQuery.setMaxResults(pageSize);
        List<User> users = (List<User>) selectQuery.getResultList();
        System.out.println("users: " + users);

        // query so item de tra ve total page
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1 ");
        if(StringUtils.hasLength(search)){
            sqlCountQuery.append(" and lower(u.fullName) like lower(:name)" +
                    " or u.phone like :phone "+
                    " or lower(u.email) like lower(:email)");
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasLength(search)){
            selectCountQuery.setParameter("name", "%"+search+"%");
            selectCountQuery.setParameter("phone", "%"+search+"%");
            selectCountQuery.setParameter("email", "%"+search+"%");
        }
        Long totalElements = (Long) selectCountQuery.getSingleResult();
        int totalPages = 0 ;
        if(totalElements % pageSize == 0){
            totalPages = (int) (totalElements/pageSize);
        }else {
            totalPages = (int) (totalElements/pageSize) + 1;
        }
        System.out.println("total elements: " + totalElements);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .items(users)
                .build();
    }
}
