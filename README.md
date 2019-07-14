# Tidbits: the web-based personal digital wallet

Tidbits is a web-based personal digital wallet. You can use it to store
passwords, logins, URLs, or just about any small tidbit of data you might easily
forget. You can then access all of this information by logging into Tidbits with
your single Tidbits password.

# Building

The build requires Apache Ant. First, clone the repository with:

```sh
ant git-clone
```

Then copy the `environment/example` files into `environment/local` and modify the local copies as 
needed.

To build the Tidbits WAR directly, use the `git-build` target:

```sh
ant -Dgit.env.src=$PWD/environment/local -Dgit.tag=2.2.0 git-fetch git-checkout git-build
```

## Building releases

```sh
ant -Dgit.tag=2.2.0 git-fetch git-checkout git-release
```
