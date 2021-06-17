package person.clp.stationeryback.service;

import person.clp.stationeryback.entity.User;

public interface UserService {
   User selectUserByUsername(String username);
}
