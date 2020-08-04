package org.dell.kube.pages;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPageRepository {
    public Page create(Page page);
    public Page read(long id);
    public List<Page> list();
    public Page update(Page page, long id);
    public void delete(long id);
}