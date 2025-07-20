package org.example.controller;
import org.example.model.User;
import org.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping ("api/v1")
public class UsersController {
    @Autowired UsersService usersService;

    @GetMapping("users")
    public ResponseEntity <?> getAllUsers (@RequestParam (defaultValue = "false") boolean detail) {
        return usersService.getAllUsers(detail);
    }

    @PostMapping("users")
    public ResponseEntity<?> addUser (@RequestBody User userToAdd) {
        return usersService.addUser(userToAdd);
   }

   @GetMapping ("users/{ID}")
    public ResponseEntity<?> userDetail (@PathVariable ("ID") long id, @RequestParam (defaultValue = "false") boolean detail) {
        //default false v request param mi ale neřeší pokud mi přijde třeba ?detail=blabla
        return usersService.userDetail(id, detail);
   }

   @PutMapping ("users")
    public ResponseEntity<?> editUser (@RequestBody User userToEdit) {
        return usersService.editUser(userToEdit);
   }

   @DeleteMapping ("users/{ID}")
    public ResponseEntity<?> deleteUser (@PathVariable ("ID") long id ) {
        return usersService.deleteUser(id);
   }

}
