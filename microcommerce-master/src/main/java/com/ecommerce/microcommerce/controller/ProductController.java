package com.ecommerce.microcommerce.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.exceptions.ProduitIntrouvableException;
import com.ecommerce.microcommerce.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Api(description = "Gestion des produits")
@RestController
public class ProductController {

    private final ProductDao productDao;

    @Autowired
    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "Produits")
    public List<Product> listeProduits(){

        return productDao.findAll();
    }



    @GetMapping(value = "ProduitsTri")
    public List<Product> trierProduitsParOrdreAlphabetique(){
        return productDao.OrderByNomAsc();
    }


    @ApiOperation(value = "Recupère un produit en fonction de son ID.")
    @GetMapping(value = "Produits/{id}")
    public Product afficherProduit(@PathVariable int id) throws ProduitIntrouvableException {

        Product produit = productDao.findById(id);

        if(produit == null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " n'existe pas !");

        return produit;
    }


    @ApiOperation(value = "Affiche la marge des produits.")
    @GetMapping(value = "AdminProduits")
    public List<String> calculerMargeProduit() {

        List<Product> produits = productDao.findAll();

        List<String> marges = new ArrayList<>();

        for (Product produit : produits) {
            int marge = produit.getPrix()-produit.getPrixAchat();
            String element = "Marge sur un(e) " + produit.getNom() + " : " + marge;
            marges.add(element);
        }
        return marges;
    }

    
    
    @GetMapping(value = "test/Produits/{prixLimit}")
    public List<Product> testRequete(@PathVariable int prixLimit){

        return productDao.chercherProduitCher(prixLimit);
    }

//----------------------------------------------------------------------------------------------------------------------
    
    @PostMapping(value = "Produits")
    public ResponseEntity<Void> ajouterProduit(/*@Valid*/ @RequestBody Product produit){

        if(produit.getPrix() == 0){
            throw new ProduitGratuitException("Le produit " + produit.getNom() + " est gratuit !!!");
        }

        Product produit1 = productDao.save(produit);

        if(produit1 == null){
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produit1.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}