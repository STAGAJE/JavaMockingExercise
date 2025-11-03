package cz.upce.fei.inptp.databasedependency.dao;

/**
 * Database access object
 * @param <T> Database object type
 */
public interface DAO<T> {

    public boolean save(T object);
    public T load(String parameters);
    public boolean delete(T object);
    
}
