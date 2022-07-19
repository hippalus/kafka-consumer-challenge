package com.company.vertical.infra.gorestclient.auth;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenProvider implements TokenProvider {

  private final Queue<String> tokens;


  public InMemoryTokenProvider() {

    //TODO: read from env
    final List<String> acc = List.of(

        "dcea584795bdfe97147e8f8be9d6eb90eb2fca9e3d97a04d711b9cbb03e4c717",
        "223cd6ec09ccc8cf145ba635054b6dce744ff4bb994fead3f3a8d19806a55599",
        "103827edcb21ba8698c524dd04e130d9d6df119027a54f10436a2475e4237a3f",
        "3a0c100113d9a864b6c94c0925081929d32fb568c539a20a58c970064d5d39d7",
        "c86c988201451f232a5f8bc27a4d7c2db92c9bc64401a5ba5e49cb23e9cbb46c",
        "e1a5195fc994dd0f9244c05b62f299c5d87793cb567c3c14f1a33979a8bc9bf4",
        "7fface9ea5fb92b9a3961c55074c00e2aa157fb7b940a6949dccbe54d6c7ac7c",
        "f49f59226b07d96b2201b8d530229f3f155f63d0af5dbca87d5f034498043531",
        "5759ff921998a6c31678f6175d484be4864c6b3d36b5776175246a06f245fc44"
    );
    this.tokens = new LinkedBlockingDeque<>(acc);
  }

  @Override
  public String current() {
    return this.tokens.peek();
  }

  @Override
  public String next() {
    this.tokens.add(this.tokens.remove());
    return this.current();
  }

  @Override
  public List<String> tokens() {
    return this.tokens.stream().toList();
  }

}
