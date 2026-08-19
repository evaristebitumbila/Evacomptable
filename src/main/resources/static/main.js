/* ============================================
   MERIDIAN — interactions
   ============================================ */

// --- Menu mobile ---
const toggle = document.querySelector('.menu-toggle');
const links = document.querySelector('.nav-links');
if (toggle) toggle.addEventListener('click', () => links.classList.toggle('open'));

// --- Reveal au scroll ---
const io = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) { e.target.classList.add('in'); io.unobserve(e.target); }
  });
}, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });
document.querySelectorAll('.reveal').forEach((el, i) => {
  el.style.transitionDelay = (i % 4) * 0.06 + 's';
  io.observe(el);
});

// ============================================
//  SÉLECTEUR DE LANGUE (FR / EN) — traduction complète du site
// ============================================
const LANG_KEY = 'eva-lang';

// Construit le dictionnaire inverse EN → FR pour pouvoir revenir au français
const DICT_FR_EN = window.TRANSLATIONS || {};
const DICT_EN_FR = {};
for (const fr in DICT_FR_EN) { DICT_EN_FR[DICT_FR_EN[fr]] = fr; }

// Parcourt tous les nœuds de texte visibles et applique la traduction
function translateTextNodes(dict) {
  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, {
    acceptNode(node) {
      const p = node.parentNode;
      if (!p) return NodeFilter.FILTER_REJECT;
      const tag = p.nodeName;
      if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT') return NodeFilter.FILTER_REJECT;
      if (!node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
      return NodeFilter.FILTER_ACCEPT;
    }
  });
  const nodes = [];
  while (walker.nextNode()) nodes.push(walker.currentNode);
  nodes.forEach(node => {
    const raw = node.nodeValue;
    const key = raw.trim();
    if (dict[key]) {
      // préserve les espaces autour du texte
      node.nodeValue = raw.replace(key, dict[key]);
    }
  });
}

// Traduit aussi les attributs (placeholder, title, alt, option, content)
function translateAttributes(dict) {
  document.querySelectorAll('[placeholder]').forEach(el => {
    const k = el.getAttribute('placeholder').trim();
    if (dict[k]) el.setAttribute('placeholder', dict[k]);
  });
  document.querySelectorAll('option').forEach(el => {
    const k = el.textContent.trim();
    if (dict[k]) el.textContent = dict[k];
  });
  document.querySelectorAll('[title]').forEach(el => {
    const k = el.getAttribute('title').trim();
    if (dict[k]) el.setAttribute('title', dict[k]);
  });
}

function applyLang(lang) {
  const dict = lang === 'en' ? DICT_FR_EN : DICT_EN_FR;
  // ne rien faire si le dictionnaire ciblé est vide (déjà dans la langue)
  translateTextNodes(dict);
  translateAttributes(dict);
  document.documentElement.setAttribute('lang', lang);
  document.querySelectorAll('.lang-switch button').forEach(b => {
    b.classList.toggle('active', b.dataset.lang === lang);
  });
  try { localStorage.setItem(LANG_KEY, lang); } catch (e) {}
}

document.querySelectorAll('.lang-switch button').forEach(btn => {
  btn.addEventListener('click', () => applyLang(btn.dataset.lang));
});

// Au chargement : applique la langue mémorisée (le HTML est en français par défaut)
(function initLang() {
  let saved = 'fr';
  try { saved = localStorage.getItem(LANG_KEY) || 'fr'; } catch (e) {}
  if (saved === 'en') applyLang('en');
  else {
    document.querySelectorAll('.lang-switch button').forEach(b => {
      b.classList.toggle('active', b.dataset.lang === 'fr');
    });
  }
})();


// ============================================
//  VALIDATION FORMULAIRE DE CONTACT
// ============================================
const form = document.querySelector('#contact-form');
if (form) {
  const validators = {
    name: v => v.trim().length >= 2,
    email: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim()),
    message: v => v.trim().length >= 10,
  };

  function validateField(field) {
    const input = field.querySelector('input, textarea');
    if (!input || !validators[input.name]) return true;
    const ok = validators[input.name](input.value);
    field.classList.toggle('invalid', !ok);
    field.classList.toggle('valid', ok && input.value.length > 0);
    return ok;
  }

  form.querySelectorAll('input, textarea').forEach(input => {
    input.addEventListener('blur', () => {
      const field = input.closest('.field');
      if (field && input.value.length > 0) validateField(field);
    });
    input.addEventListener('input', () => {
      const field = input.closest('.field');
      if (field && field.classList.contains('invalid')) validateField(field);
    });
  });

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    let allOk = true;
    form.querySelectorAll('.field').forEach(field => {
      const input = field.querySelector('input, textarea');
      if (input && validators[input.name]) {
        if (!validateField(field)) allOk = false;
      }
    });
    if (!allOk) {
      const firstErr = form.querySelector('.field.invalid');
      if (firstErr) firstErr.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }
    const btn = form.querySelector('button[type="submit"]');
    const lang = document.documentElement.getAttribute('lang') || 'fr';
    btn.textContent = lang === 'en' ? 'Message sent ✓' : 'Message envoyé ✓';
    btn.style.background = 'var(--accent)';
    btn.style.color = 'var(--ink)';
    form.reset();
    form.querySelectorAll('.field').forEach(f => f.classList.remove('valid', 'invalid'));
    setTimeout(() => {
      btn.textContent = lang === 'en' ? 'Send message' : 'Envoyer le message';
      btn.style.background = '';
      btn.style.color = '';
    }, 3500);
  });
}

// ============================================
//  NEWSLETTER (footer)
// ============================================
const news = document.querySelector('.footer-news');
if (news) {
  news.addEventListener('submit', (e) => {
    e.preventDefault();
    const input = news.querySelector('input');
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input.value.trim())) {
      input.style.borderColor = '#c0392b';
      return;
    }
    news.classList.add('done');
    const lang = document.documentElement.getAttribute('lang') || 'fr';
    input.value = '';
    input.setAttribute('placeholder', lang === 'en' ? 'Subscribed ✓' : 'Inscription confirmée ✓');
    input.style.borderColor = '';
  });
}


/* ============================================================
   ANIMATIONS — hero, parallaxe, compteurs, curseur, particules,
   smooth scroll, transitions de page
   Respecte prefers-reduced-motion et les écrans tactiles.
   ============================================================ */
(function () {
  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const touch = window.matchMedia('(hover: none)').matches;

  /* --- 1. Hero : animation d'entrée au chargement --- */
  const hero = document.querySelector('.hero');
  if (hero) {
    requestAnimationFrame(() => {
      requestAnimationFrame(() => document.body.classList.add('hero-loaded'));
    });
  }

  /* --- 2. Transition d'entrée de page --- */
  document.body.classList.add('page-enter');

  /* --- 3. Compteurs animés (chiffres qui montent) --- */
  function animateCount(el) {
    const target = parseFloat(el.dataset.count);
    if (isNaN(target)) return;
    if (reduce) { el.textContent = target; return; }
    const dur = 1400;
    const start = performance.now();
    function tick(now) {
      const p = Math.min((now - start) / dur, 1);
      const eased = 1 - Math.pow(1 - p, 3); // easeOutCubic
      el.textContent = Math.round(target * eased);
      if (p < 1) requestAnimationFrame(tick);
      else el.textContent = target;
    }
    requestAnimationFrame(tick);
  }
  const countObs = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) { animateCount(e.target); countObs.unobserve(e.target); }
    });
  }, { threshold: 0.5 });
  document.querySelectorAll('.count[data-count]').forEach(el => countObs.observe(el));

  /* --- 4. Effet parallaxe sur la lueur du hero --- */
  if (!reduce && hero) {
    let ticking = false;
    window.addEventListener('scroll', () => {
      if (ticking) return;
      ticking = true;
      requestAnimationFrame(() => {
        const y = window.scrollY;
        if (y < window.innerHeight) {
          hero.style.setProperty('--par', (y * 0.35) + 'px');
          const glow = hero;
          glow.style.backgroundPositionY = (y * 0.2) + 'px';
        }
        ticking = false;
      });
    }, { passive: true });
  }

  /* --- 5. Curseur personnalisé animé --- */
  if (!reduce && !touch) {
    const dot = document.createElement('div'); dot.className = 'cursor-dot';
    const ring = document.createElement('div'); ring.className = 'cursor-ring';
    document.body.append(dot, ring);
    document.body.classList.add('has-custom-cursor');

    let mx = 0, my = 0, rx = 0, ry = 0;
    document.addEventListener('mousemove', (e) => {
      mx = e.clientX; my = e.clientY;
      dot.style.left = mx + 'px'; dot.style.top = my + 'px';
    });
    (function loop() {
      rx += (mx - rx) * 0.18;
      ry += (my - ry) * 0.18;
      ring.style.left = rx + 'px'; ring.style.top = ry + 'px';
      requestAnimationFrame(loop);
    })();

    document.querySelectorAll('a, button, input, textarea, select').forEach(el => {
      el.addEventListener('mouseenter', () => ring.classList.add('hovering'));
      el.addEventListener('mouseleave', () => ring.classList.remove('hovering'));
    });
  }

  /* --- 6. Particules sur le canvas du hero --- */
  const canvas = document.querySelector('.hero-particles');
  if (canvas && !reduce) {
    const ctx = canvas.getContext('2d');
    let w, h, particles, raf;
    const COUNT = 56;

    function size() {
      w = canvas.width = hero.offsetWidth;
      h = canvas.height = hero.offsetHeight;
    }
    function make() {
      particles = Array.from({ length: COUNT }, () => ({
        x: Math.random() * w,
        y: Math.random() * h,
        r: Math.random() * 1.8 + 0.4,
        vx: (Math.random() - 0.5) * 0.3,
        vy: (Math.random() - 0.5) * 0.3,
        a: Math.random() * 0.5 + 0.2,
      }));
    }
    function draw() {
      ctx.clearRect(0, 0, w, h);
      for (let i = 0; i < particles.length; i++) {
        const p = particles[i];
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0 || p.x > w) p.vx *= -1;
        if (p.y < 0 || p.y > h) p.vy *= -1;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(255,210,63,${p.a})`;
        ctx.fill();
        // liens entre particules proches
        for (let j = i + 1; j < particles.length; j++) {
          const q = particles[j];
          const dx = p.x - q.x, dy = p.y - q.y;
          const dist = Math.hypot(dx, dy);
          if (dist < 120) {
            ctx.beginPath();
            ctx.moveTo(p.x, p.y); ctx.lineTo(q.x, q.y);
            ctx.strokeStyle = `rgba(124,127,232,${0.12 * (1 - dist / 120)})`;
            ctx.lineWidth = 0.6;
            ctx.stroke();
          }
        }
      }
      raf = requestAnimationFrame(draw);
    }
    size(); make(); draw();
    let rt;
    window.addEventListener('resize', () => {
      clearTimeout(rt);
      rt = setTimeout(() => { size(); make(); }, 200);
    });
  }

  /* --- 7. Smooth scroll pour les ancres internes --- */
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', (e) => {
      const id = a.getAttribute('href');
      if (id.length < 2) return;
      const target = document.querySelector(id);
      if (!target) return;
      e.preventDefault();
      target.scrollIntoView({ behavior: reduce ? 'auto' : 'smooth', block: 'start' });
    });
  });

  /* --- 8. Transition de sortie de page (fondu) --- */
  if (!reduce) {
    const fade = document.createElement('div');
    fade.className = 'page-fade';
    document.body.appendChild(fade);
    document.querySelectorAll('a[href]').forEach(a => {
      const href = a.getAttribute('href');
      const sameTab = !a.target || a.target === '_self';
      const internal = href && !href.startsWith('#') && !href.startsWith('http') && !href.startsWith('mailto');
      if (internal && sameTab) {
        a.addEventListener('click', (e) => {
          e.preventDefault();
          fade.classList.add('run');
          setTimeout(() => { window.location.href = href; }, 480);
        });
      }
    });
  }
})();


/* ============================================
   ACTUALITÉS — carrousel 3D (coverflow)
   ============================================ */
(function () {
  const carousel = document.getElementById('carousel3d');
  if (!carousel) return;
  const stage = document.getElementById('c3d-stage');
  const pubs = window.PUBLICATIONS || [];
  if (stage && pubs.length) {
    stage.innerHTML = pubs.map(function (p) {
      var link = p.lien ? '<a href="' + p.lien + '" class="news-link">' + (p.lienTexte || 'En savoir plus') +
        ' <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M13 6l6 6-6 6"/></svg></a>' : '';
      return '<article class="c3d-card"' + (p.lien ? ' data-href="' + p.lien + '"' : '') + '>' +
        '<div class="news-img" style="background-image:url(\'' + p.image + '\')"></div>' +
        '<div class="news-body"><span class="news-date">' + (p.badge || '') + '</span>' +
        '<h3>' + p.titre + '</h3><p>' + p.texte + '</p>' + link + '</div></article>';
    }).join('');
  }
  const cards = Array.from(carousel.querySelectorAll('.c3d-card'));
  const prevBtn = document.getElementById('c3d-prev');
  const nextBtn = document.getElementById('c3d-next');
  const dotsWrap = document.getElementById('c3d-dots');
  const n = cards.length;
  if (!n) return;

  let current = 0;
  let timer = null;
  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  dotsWrap.innerHTML = '';
  cards.forEach((_, i) => {
    const dot = document.createElement('button');
    dot.className = 'c3d-dot' + (i === 0 ? ' active' : '');
    dot.setAttribute('aria-label', 'Aller à la publication ' + (i + 1));
    dot.addEventListener('click', () => { go(i); restart(); });
    dotsWrap.appendChild(dot);
  });
  const dots = Array.from(dotsWrap.children);

  function render() {
    cards.forEach((card, i) => {
      let cls = 'pos-hidden';
      if (i === current) cls = 'pos-center';
      else if (i === (current - 1 + n) % n) cls = 'pos-left';
      else if (i === (current + 1) % n) cls = 'pos-right';
      card.className = 'c3d-card ' + cls;
    });
    dots.forEach((d, i) => d.classList.toggle('active', i === current));
  }
  function go(i) { current = (i + n) % n; render(); }
  function next() { go(current + 1); }
  function prev() { go(current - 1); }

  cards.forEach((card, i) => {
    card.addEventListener('click', () => {
      if (i !== current) { go(i); restart(); }
      else if (card.dataset.href) { window.location.href = card.dataset.href; }
    });
  });

  if (nextBtn) nextBtn.addEventListener('click', () => { next(); restart(); });
  if (prevBtn) prevBtn.addEventListener('click', () => { prev(); restart(); });

  function start() { if (!reduce) timer = setInterval(next, 4000); }
  function restart() { clearInterval(timer); start(); }
  carousel.addEventListener('mouseenter', () => clearInterval(timer));
  carousel.addEventListener('mouseleave', () => start());

  render();
  start();
})();


/* ============================================
   WIDGET WHATSAPP — ouvrir / fermer le panneau
   ============================================ */
(function () {
  const widget = document.getElementById('wa-widget');
  const fab = document.getElementById('wa-fab');
  const closeBtn = document.getElementById('wa-close');
  const icon = document.getElementById('wa-fab-icon');
  if (!widget || !fab) return;

  function toggle() {
    const open = widget.classList.toggle('open');
    // change l'icône : whatsapp quand fermé, croix quand ouvert
    if (icon) icon.className = open ? 'fa-solid fa-xmark' : 'fa-brands fa-whatsapp';
  }
  fab.addEventListener('click', toggle);
  if (closeBtn) closeBtn.addEventListener('click', () => {
    widget.classList.remove('open');
    if (icon) icon.className = 'fa-brands fa-whatsapp';
  });
  // fermer si on clique en dehors
  document.addEventListener('click', (e) => {
    if (widget.classList.contains('open') && !widget.contains(e.target)) {
      widget.classList.remove('open');
      if (icon) icon.className = 'fa-brands fa-whatsapp';
    }
  });
})();


/* ============================================
   FAQ — accordéon
   ============================================ */
(function () {
  const items = document.querySelectorAll('.faq-item');
  if (!items.length) return;
  items.forEach(item => {
    const q = item.querySelector('.faq-q');
    q.addEventListener('click', () => {
      const isOpen = item.classList.contains('open');
      // fermer tous les autres
      items.forEach(i => i.classList.remove('open'));
      // ouvrir celui-ci si il était fermé
      if (!isOpen) item.classList.add('open');
    });
  });
})();


/* ============================================
   PROGRAMME FORMATION — accordéon
   ============================================ */
(function () {
  const items = document.querySelectorAll('.prog-item');
  if (!items.length) return;
  items.forEach(item => {
    const head = item.querySelector('.prog-head');
    head.addEventListener('click', () => {
      const wasOpen = item.classList.contains('open');
      items.forEach(i => i.classList.remove('open'));
      if (!wasOpen) item.classList.add('open');
    });
  });
  // ouvrir le premier par défaut
  items[0].classList.add('open');
})();
