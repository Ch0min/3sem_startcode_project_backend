package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.UserDTO;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import entities.User;
import facades.UserFacade;
import utils.EMF_Creator;

@Path("info")
public class UserResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade facade = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }


    /* RESOURCE METHODS */

    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllUsers() {
        return Response.ok().entity(GSON.toJson(facade.getAllUsers())).build();
    }

    @GET
    @Path("user/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserByID(@PathParam("id") Long id) {
        UserDTO userId = facade.getUserByID(id);
        return Response.ok().entity(GSON.toJson(userId)).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createUser(String content) {
        UserDTO udto = facade.createUser(GSON.fromJson(content, UserDTO.class));
        return Response.ok().entity(GSON.toJson(udto)).build();
    }

    @PUT
    @Path("user/update/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String updateUser(@PathParam("id") long id, String content) {
        UserDTO userDTO = GSON.fromJson(content, UserDTO.class);
        userDTO.setId(id);
        userDTO = facade.updateUser(userDTO);
        return GSON.toJson(userDTO);
    }


    @DELETE
    @Path("user/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteUser(@PathParam("id") long id) {
        UserDTO userDeleted = facade.deleteUser(id);
        return Response.ok().entity(GSON.toJson(userDeleted)).build();
    }
}