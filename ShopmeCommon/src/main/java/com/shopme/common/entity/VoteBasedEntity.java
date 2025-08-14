package com.shopme.common.entity;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class VoteBasedEntity extends IdBasedEntity {
    protected static final int VOTE_UP_POINT = 1;
    protected static final int VOTE_DOWN_POINT = -1;

    protected int votes;

    public void voteUp() {
        this.votes = VOTE_UP_POINT;
    }

    public void voteDown() {
        this.votes = VOTE_DOWN_POINT;
    }

    @Transient
    public boolean isUpVoted() {
        return this.votes == VOTE_UP_POINT;
    }

    @Transient
    public boolean isDownVoted() {
        return this.votes == VOTE_DOWN_POINT;
    }
}
