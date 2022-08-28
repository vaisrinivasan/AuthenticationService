package com.org.authservice.dao;

import com.org.authservice.models.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlQuery("select * from users where email = :email or username = :username or password = crypt(:password, password)")
    public User getUser(@Bind("email") final String email,
                                 @Bind("username") final String username,
                                 @Bind("password") final String password);

    @SqlUpdate("insert into users values(:id, :username, :email, crypt(:password, gen_salt('bf',8)))")
    public int createUser(@BindBean User user);

    @SqlQuery("select * from users where username = :username and password = crypt(:password, password)")
    public User getRegisteredUser(@Bind("username") final String username,
                                  @Bind("password") final String password);
}
