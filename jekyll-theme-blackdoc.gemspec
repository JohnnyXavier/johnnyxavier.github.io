# coding: utf-8

Gem::Specification.new do |spec|
  spec.name          = "jekyll-theme-ioforge"
  spec.version       = "0.91"
  spec.authors       = ["Johnny Xavier"]

  spec.summary       = %q{Black Theme for Jekyll based on BlackDoc}
  spec.homepage      = "https://github.com/johnnyxavier/ioforgetheme"
  spec.license       = "MIT"

  spec.metadata["plugin_type"] = "theme"

  spec.files         = `git ls-files -z`.split("\x0").select do |f|
    f.match(%r{^(public|_(includes|layouts)/|(LICENSE|README)((\.(txt|md|markdown)|$)))}i)
  end

  spec.bindir        = "exe"
  spec.executables   = spec.files.grep(%r{^exe/}) { |f| File.basename(f) }

  spec.add_runtime_dependency "jekyll", "~> 3.7"
  spec.add_runtime_dependency "jekyll-gist", "~> 1.5"
  spec.add_runtime_dependency "jekyll-paginate", "~> 1.1"
  spec.add_runtime_dependency "jekyll-feed", "~> 0.6"
  spec.add_runtime_dependency "jekyll-seo-tag", "~> 2.5.0"
  spec.add_runtime_dependency "jekyll-sitemap", "~> 1.2.0"
  spec.add_development_dependency "bundler", "~> 1.12"
end
