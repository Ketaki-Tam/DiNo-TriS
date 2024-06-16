package com.example.mongoTriples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mongo_triple")
@RequiredArgsConstructor
public class TripleController {
    private final TripleService tripleService;

    @GetMapping("/query")
    public List<Triple> query(@RequestParam("subject") String subject)
    {
        return this.tripleService.query(subject);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Triple theTriple)
    {
        return this.tripleService.update(theTriple);
    }

    @GetMapping("/merge")
    public Boolean merge(@RequestParam("serverID") String serverID) throws IOException {
        return this.tripleService.merge(serverID);
    }

    @PostMapping("/mergeRequest")
    public List<UpdateLog> mergeRequest(@RequestBody List<UpdateLog> updateLogs,@RequestParam("serverID") String serverID)
    {
        return this.tripleService.mergeRequest(updateLogs,serverID);
    }

    @GetMapping("/insertAll")
    public Boolean insertAll()
    {
        return this.tripleService.insertAll();
    }



}
